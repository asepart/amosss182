import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity} from 'react-native';
import styles from '../Login/Design';
import {
	StackNavigator,
  } from 'react-navigation';

export default class Projects extends Component {

  static navigationOptions= {
		title: 'Projects',
		headerStyle: {
			backgroundColor:'#8eacbb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
    } 
    
    onAddPressed() {

			//navigate to different site
		const { navigate } = this.props.navigation;
		navigate("Third", { name: "JoinProject" })

		} 
	
		
	
	

    render() {
      var {params} = this.props.navigation.state;
      return (
        <View style={styles.container}>
            <TouchableOpacity 
           onPress={this.onAddPressed.bind(this)} 
            style={styles.addButton}>
			
				<Text style={styles.buttonText && styles.textLarge}>+</Text>

			</TouchableOpacity>
        
        </View>
      );
    }
  }
  
