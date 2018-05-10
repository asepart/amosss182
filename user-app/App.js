import React, {Component} from 'react';
import { StyleSheet, Text, View, AppRegistry} from 'react-native';

import Login from './src/components/Login/login';
import SecondScreen from './src/components/secondScreen';
import Projects from '../user-app/src/components/Projects/projects';
import JoinProject from '../user-app/src/components/Projects/joinProject';

import {
  StackNavigator,
} from 'react-navigation';

const AppNavigation = StackNavigator({
  First: { screen: Login },
  Second: { screen: Projects },
  Third: {screen: JoinProject}
});
export default AppNavigation;

AppRegistry.registerComponent('user-app', () => AppNavigation);

