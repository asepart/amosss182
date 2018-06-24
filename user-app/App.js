import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry} from 'react-native';
import ProjectInfo from './src/components/Projects/projectInfo';
import Login from './src/components/Login/login';
import JoinProject from '../user-app/src/components/Projects/joinProject';
import GetMessages from './src/components/Chat/messages';
import TicketView from '../user-app/src/components/Projects/ticketView';
import TicketProcessing from './src/components/Tickets/ticketProcessed';
import Camera from '../user-app/src/components/MediaSupport/camera';

import {
  StackNavigator,
} from 'react-navigation';

const AppNavigation = StackNavigator({
  First: { screen: Login },
  Third: {screen: JoinProject},
  Fourth: {screen: ProjectInfo},
  Sixth: {screen: TicketView, 
     navigationOptions: ({navigation}) => ({
    id: navigation.state.params.id,
    headerStyle: {
      backgroundColor:'#5daedb'
  },
	headerTitleStyle: {
		color:'#FFF'
	}
  })},
  Seventh: {screen: GetMessages},
  Eigth: {screen: TicketProcessing },
 // Ninth: {screen: Camera }

 

}); 
export default AppNavigation;

AppRegistry.registerComponent('user-app', () => AppNavigation);
