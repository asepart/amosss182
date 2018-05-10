import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity} from 'react-native';
import styles from '../Login/Design';
import {setState} from '../Login/auth';
import {
	StackNavigator,
  } from 'react-navigation';


export default class JoinProject extends Component {

  static navigationOptions= {
		title: 'Join Projects',
		headerStyle: {
			backgroundColor:'#8eacbb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
    } 
    
    constructor() {
		super();

		this.state = {
			entryKey: "",
			error: "",
	}
  }
  

    render() {
      var {params} = this.props.navigation.state;
      return (
        <View style={styles.container}>
        <TextInput 
       // onChangeText={(text) => this.setState({entryKey: text})} 
        placeholder="Entry Key" placeholderTextColor="#FFF" style={styles.input}/>
            <TouchableOpacity 
           // onPress={this.onJoinPressed.bind(this)} 
            style={styles.buttonContainer}>
			
				<Text style={styles.buttonText}>JOIN PROJECT</Text>

			</TouchableOpacity>
        
        </View>
      );
    }
  }
  
