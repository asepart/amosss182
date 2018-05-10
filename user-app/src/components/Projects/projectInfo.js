import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity} from 'react-native';
import styles from '../Login/Design';
import {
	StackNavigator,
  } from 'react-navigation';

export default class ProjectInfo extends Component {

  static navigationOptions= {
		title: 'Project Info',
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
           
			
				<Text style={styles.buttonText && styles.textLarge}>Here will be project info if key was valid</Text>

		
        
        </View>
      );
    }
  }