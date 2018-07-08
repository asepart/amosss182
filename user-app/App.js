import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry} from 'react-native';
import Login from './src/components/Login/login';
import GetMessages from './src/components/Chat/messages';
import TicketView from '../user-app/src/components/Projects/ticketView';
import TicketProcessing from './src/components/Tickets/ticketProcessed';
import CameraRollPicker from '../user-app/src/components/MediaSupport/cameraRollPicker';
import Camera from '../user-app/src/components/MediaSupport/camera';
import UserInfo from './src/components/Login/userInfo';
import ProjectListTicketList from '../user-app/src/components/Projects/projectListTicketList';

import {
  createStackNavigator
} from 'react-navigation';

import ProjectList from './src/components/Projects/projectList';

const AppNavigation = createStackNavigator({
  First: { screen: Login },
  Sixth: {screen: TicketView, 
     navigationOptions: ({navigation}) => ({
    id: navigation.state.params.id,
    headerStyle: {
      backgroundColor:'#5daedb',
      paddingRight: 15
  },
	headerTitleStyle: {
		color:'#FFF'
	}
  })},
  Seventh: {screen: GetMessages},
  Eigth: {screen: TicketProcessing },
  Ninth: {screen: Camera },
  Tenth: {screen:ProjectList},
  Eleventh: {screen: CameraRollPicker},
  Twelfth: {screen: ProjectListTicketList,
    navigationOptions: ({navigation}) => ({
      entryKey: navigation.state.params.entrykey,
      headerStyle: {
        backgroundColor:'#5daedb',
        paddingRight: 15
    },
      headerTitleStyle: {
          color:'#FFF'
      }
    })},
  Thirteenth: {screen: UserInfo}
}); 
export default AppNavigation;

AppRegistry.registerComponent('user-app', () => AppNavigation);