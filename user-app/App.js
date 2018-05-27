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


const DrawerExample = DrawerNavigator(
  { 
    First: {
      screen: stackNav,
      navigationOptions: {
        drawerLabel: "Login",
       // drawerIcon: ({ tintColor }) => <MaterialIcons
       // name= "rocket"
        //size= {24}  
       // style={{color: tintColor}}
       // >
      //    </MaterialIcons>
    },
    },

    Second: {
      screen: JoinProject

}},
{
initialRouteName: 'First',
drawerPosition: 'left',
drawerWidth: 150,
contentOptions: {
  activeTintColor: '#0c3868'
}
}
);

export default DrawerExample;

AppRegistry.registerComponent('user-app', () => DrawerExample);
