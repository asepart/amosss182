import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry} from 'react-native';
import ProjectInfo from './src/components/Projects/projectInfo';
import Login from './src/components/Login/login';
import JoinProject from '../user-app/src/components/Projects/joinProject';
import GetMessages from './src/components/Chat/messages';

import {
  StackNavigator,
} from 'react-navigation';

const AppNavigation = StackNavigator({
  First: { screen: Login },
  Third: {screen: JoinProject},
  Fourth: {screen: ProjectInfo},
  Sixth: {screen: GetMessages}
 

}); 
export default AppNavigation;

AppRegistry.registerComponent('user-app', () => AppNavigation);