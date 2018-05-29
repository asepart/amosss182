import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry} from 'react-native';
import ProjectInfo from '../Projects/projectInfo';
import Login from '../Login/login';
import JoinProject from '../Projects/joinProject';
import GetMessages from '../Chat/messages';
import TicketDetails from '../Projects/ticketView';
import SecondScreen from '../secondScreen';



import {
  StackNavigator, DrawerNavigator
} from 'react-navigation';

const DrawerExample = DrawerNavigator(
    { 
      First: 
      {
        screen: SecondScreen,
        navigationOptions: {
          drawerLabel: "Home",
          title: 'Home',
  
         // drawerIcon: ({ tintColor }) => <MaterialIcons
         // name= "rocket"
          //size= {24}  
         // style={{color: tintColor}}
         // >
        //    </MaterialIcons>
      },
      },
  
      Second: {
        screen: JoinProject,
        navigationOptions: {
            drawerLabel: "Join Project",
            title: 'Join Project',
      headerStyle: {
          backgroundColor:'#5daedb'
      },
      headerTitleStyle: {
          color:'#FFF'
      }
    },
},

    Third: {
      screen: Login,
      navigationOptions: {
        drawerLabel: "Login",
        title: 'Login',
  headerStyle: {
      backgroundColor:'#5daedb'
  },
  headerTitleStyle: {
      color:'#FFF'
  }
},
    }

   
},
  {
  initialRouteName: 'First',
  drawerPosition: 'left',
  drawerWidth: 150,
  contentOptions: {
    activeTintColor: '#0c3868'
  },
  }
  );
  
  export default DrawerExample;
