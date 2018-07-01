import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry, Button} from 'react-native';
import ProjectInfo from './src/components/Projects/projectInfo';
import Login from './src/components/Login/login';
import JoinProject from '../user-app/src/components/Projects/joinProject';
import GetMessages from './src/components/Chat/messages';
import TicketView from '../user-app/src/components/Projects/ticketView';
import TicketProcessing from './src/components/Tickets/ticketProcessed';
import CameraRollPicker from '../user-app/src/components/MediaSupport/cameraRollPicker';
import Camera from '../user-app/src/components/MediaSupport/camera';

import {
  createStackNavigator, DrawerNavigator
} from 'react-navigation';
import stackNav from '../user-app/src/components/Navigation/stackNav';
import styles from '../user-app/src/components/Login/Design';

import ProjectList from './src/components/Projects/projectList';

const AppNavigation = createStackNavigator({
  First: { screen: Login },
  Second: { screen: stackNav,
    navigationOptions: ({navigation}) => ({
      headerStyle: {backgroundColor: '#5daedb'},
      title: 'Welcome!',
      headerTintColor: 'white',
      headerLeft: <Text style={styles.text} onPress={() => 
      navigation.navigate('DrawerOpen')}>Menu</Text>
    }) },
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
  Ninth: {screen: Camera },
  Tenth: {screen:ProjectList},
  Eleventh: {screen: CameraRollPicker}
      

}); 
export default AppNavigation;

AppRegistry.registerComponent('user-app', () => AppNavigation);