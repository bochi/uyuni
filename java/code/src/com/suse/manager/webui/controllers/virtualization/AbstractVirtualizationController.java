/**
 * Copyright (c) 2020 SUSE LLC
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package com.suse.manager.webui.controllers.virtualization;

import com.redhat.rhn.common.hibernate.LookupException;
import com.redhat.rhn.domain.server.Server;
import com.redhat.rhn.domain.user.User;
import com.redhat.rhn.manager.rhnset.RhnSetDecl;
import com.redhat.rhn.manager.system.SystemManager;

import com.suse.manager.webui.errors.NotFoundException;
import com.suse.manager.webui.services.iface.VirtManager;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * Base controller class offering convenience functions for other virtualization-related controllers
 */
public abstract class AbstractVirtualizationController {

    private final String jadeTemplatesPath;
    protected final VirtManager virtManager;

    /**
     * @param virtManagerIn instance to manage virtualization
     * @param jadeTemplatesPathIn the path to the folder containing the Jade templates
     */
    public AbstractVirtualizationController(VirtManager virtManagerIn, String jadeTemplatesPathIn) {
        this.virtManager = virtManagerIn;
        this.jadeTemplatesPath = jadeTemplatesPathIn;
    }

    /**
     * Extract the server ID from the request. The Server ID should be in the sid Spark parameter.
     *
     * @param request the Spark request
     * @return the server Id
     *
     * @throws NotFoundException if the server id has an invalid format or if the sid parameter can't be found
     */
    protected long getServerId(Request request) throws NotFoundException {
        try {
            return Long.parseLong(request.params("sid"));
        }
        catch (NumberFormatException e) {
            throw Spark.halt(HttpStatus.SC_NOT_FOUND, "Invalid server id: " + request.params("sid"));
        }
    }

    /**
     * Get the server from the request. The Server ID should be in the sid Spark parameter.
     *
     * @param request the Spark request
     * @return the server Id
     *
     * @throws NotFoundException if the server can't be found in the request or in the database
     */
    protected Server getServer(Request request, User user) throws NotFoundException {
        Server server;
        long serverId = getServerId(request);

        try {
            server = SystemManager.lookupByIdAndUser(serverId, user);
        }
        catch (LookupException e) {
            throw new NotFoundException();
        }
        return server;
    }

    /**
     * Displays a page server-related virtual page
     *
     * @param request the request
     * @param response the response
     * @param user the user
     * @param template the name to the Jade template of the page
     * @param modelExtender provides additional properties to pass to the Jade template
     * @return the ModelAndView object to render the page
     */
    protected ModelAndView renderPage(Request request, Response response, User user,
                                    String template,
                                    Supplier<Map<String, Object>> modelExtender) {
        Map<String, Object> data = new HashMap<>();
        Server server = getServer(request, user);

        /* For system-common.jade */
        data.put("server", server);
        data.put("inSSM", RhnSetDecl.SYSTEMS.get(user).contains(server.getId()));

        if (modelExtender != null) {
            data.putAll(modelExtender.get());
        }

        /* For the rest of the template */

        return new ModelAndView(data, String.format("%s/%s.jade", jadeTemplatesPath, template));
    }
}
