import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry} from 'react-native';
import ProjectInfo from './src/components/Projects/projectInfo';
import Login from './src/components/Login/login';
import JoinProject from '../user-app/src/components/Projects/joinProject';
import GetMessages from './src/components/Chat/messages';
import TicketDetails from '../user-app/src/components/Projects/ticketView';
import stackNav from '../user-app/src/components/Navigation/stackNav';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';



import {
  StackNavigator, DrawerNavigator
} from 'react-navigation';

const AppNavigation = StackNavigator({
  First: { screen: Login },
  Second: {screen: stackNav  }}, {
    headerMode: 'float',
    navigationOptions: ({navigation}) => ({
      headerStyle: {backgroundColor: '#5daedb'},
      title: 'Home',
     
      headerTintColor: 'white',
    }),
  Third: {screen: JoinProject,
    title: 'Join Projects',
    headerStyle: {
        backgroundColor:'#5daedb'
    },
    headerTitleStyle: {
        color:'#FFF'
    }
},
  Fourth: {screen: ProjectInfo},
  Sixth: {screen: GetMessages, 
     navigationOptions: ({navigation}) => ({
    id: navigation.state.params.id,
    headerStyle: {
      backgroundColor:'#5daedb'
  },
	headerTitleStyle: {
		color:'#FFF'
	}
  })},
  Seventh: {screen: TicketDetails}
 
 

}); 

export default AppNavigation;

AppRegistry.registerComponent('user-app', () => AppNavigation);
