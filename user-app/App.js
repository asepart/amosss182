import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry, Button} from 'react-native';
import ProjectInfo from './src/components/Projects/projectInfo';
import Login from './src/components/Login/login';
import JoinProject from '../user-app/src/components/Projects/joinProject';
import GetMessages from './src/components/Chat/messages';
import TicketView from '../user-app/src/components/Projects/ticketView';
import stackNav from '../user-app/src/components/Navigation/stackNav';
import styles from '../user-app/src/components/Login/Design';

import {
  StackNavigator,DrawerNavigator
} from 'react-navigation';

const AppNavigation = StackNavigator({
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
      })
  },
  Seventh: {screen: GetMessages}
 

}); 
export default AppNavigation;

AppRegistry.registerComponent('user-app', () => AppNavigation);