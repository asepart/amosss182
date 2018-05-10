import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry} from 'react-native';
import ProjectInfo from './src/components/Projects/projectInfo';
import Login from './src/components/Login/login';
import Projects from '../user-app/src/components/Projects/projects';
import JoinProject from '../user-app/src/components/Projects/joinProject';

import {
  StackNavigator,
} from 'react-navigation';

const AppNavigation = StackNavigator({
  First: { screen: Login },
  Second: { screen: Projects },
  Third: {screen: JoinProject},
  Fourth: {screen: ProjectInfo}
}); 
export default AppNavigation;

AppRegistry.registerComponent('user-app', () => AppNavigation);