import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry} from 'react-native';
import ProjectInfo from '../Projects/projectInfo';
import Login from '../Login/login';
import JoinProject from '../Projects/joinProject';
import GetMessages from '../Chat/messages';
import TicketDetails from '../Projects/ticketView';



import {
  StackNavigator, DrawerNavigator
} from 'react-navigation';

const AppNavigation = StackNavigator({
  First: { screen: Login },
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