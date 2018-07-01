import React, {Component} from 'react';
import { StyleSheet, Text, View} from 'react-native';
import styles from '../components/Login/Design';
import {username} from '../components/Login/login';

export default class SecondScreen extends Component {

  static navigationOptions= {
		title: 'SecondScreen',
		headerStyle: {
			backgroundColor:'#8eacbb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
	} 
    render() {
      var {params} = this.props.navigation.state;
      return (
        <View style={styles.container}>
        <Text style={styles.textLarge}>Welcome back </Text>
        </View>
      );
    }
  }
  
 