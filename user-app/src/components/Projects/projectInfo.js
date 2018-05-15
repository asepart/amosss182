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
           <TouchableOpacity 
           onPress={()=>{
            const { navigate } = this.props.navigation;
            navigate("Sixth", { name: "GetMessages" })
           }} 
            style={styles.buttonContainer}>
			
				<Text style={styles.buttonText}>Enter Chat</Text>
          
			</TouchableOpacity>
			
				
		
        
        </View>
      );
    }
  }