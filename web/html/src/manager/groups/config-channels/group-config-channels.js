/* eslint-disable */
'use strict';

import * as React from 'react';
import ReactDOM from 'react-dom';
import { ConfigChannels } from 'components/config-channels';
import { Utils as MessagesUtils } from 'components/messages';
import Network from 'utils/network';
import SpaRenderer from 'core/spa/spa-renderer';

function matchUrl(target) {
    return "/rhn/manager/api/states/match?id=" + groupId + "&type=GROUP"
             + (target ? "&target=" + target : "");
}

function applyRequest(component) {
    return Network.post(
        "/rhn/manager/api/states/apply",
        JSON.stringify({
            id: groupId,
            type: "GROUP",
            states: ["custom_groups"]
        }),
        "application/json"
        )
        .promise.then( data => {
          console.log("apply action queued:" + data)
          component.setState({
              messages: MessagesUtils.info(t("Applying the config channels has been scheduled for each minion server in this group"))
          });
        });
}

function saveRequest(states) {
    return Network.post(
        "/rhn/manager/api/states/save",
        JSON.stringify({
             id: groupId,
             type: "GROUP",
             channels: states
         }),
        "application/json"
    );
}

export const renderer = () => SpaRenderer.renderNavigationReact(
  <ConfigChannels matchUrl={matchUrl} saveRequest={saveRequest} applyRequest={applyRequest}/>,
  document.getElementById('config-channels')
);
