
--
-- Copyright (c) 2008--2011 Red Hat, Inc.
--
-- This software is licensed to you under the GNU General Public License,
-- version 2 (GPLv2). There is NO WARRANTY for this software, express or
-- implied, including the implied warranties of MERCHANTABILITY or FITNESS
-- FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
-- along with this software; if not, see
-- http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
-- 
-- Red Hat trademarks are not licensed under GPLv2. No permission is
-- granted to use or replicate Red Hat trademarks that are incorporated
-- in this software or its documentation. 
--

create or replace trigger
rhn_srv_net_iface_mod_trig
before insert or update on rhnServerNetInterface
for each row
begin
        if :new.id is null then
			select rhn_srv_net_iface_id_seq.nextval into :new.id from dual;
		end if;
        :new.modified := sysdate;
end;
/
show errors
