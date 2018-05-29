import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity} from 'react-native';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {setKey, isValid} from './keyValid';

import {
	StackNavigator,
  } from 'react-navigation';


export default class JoinProject extends Component {

  static navigationOptions= {
		title: 'Join Projects',
		headerStyle: {
			backgroundColor:'#5daedb'
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
  
 async onJoinPressed() {


      setKey(this.state.entryKey);
  
     if(await isValid()){
        setState({isValid: true});
  
  
        //navigate to different site
       const { navigate } = this.props.navigation;
        navigate("Fourth", { name: "ProjectInfo" })
  
     } else {
        this.setState({error: "something went wrong"});
      }
    
      
    
    }s


    render() {
      var {params} = this.props.navigation.state;
      return (
        <View style={styles.container}>
        <TextInput 
         onChangeText={(text) => this.setState({entryKey: text})} 
        placeholder="Entry Key" placeholderTextColor="#FFF" underlineColorAndroid="transparent" style={styles.input}/>
            <TouchableOpacity 
           onPress={this.onJoinPressed.bind(this)} 
            style={styles.buttonContainer}>
			
				<Text style={styles.buttonText}>Join Project</Text>
          
			</TouchableOpacity>
        
      <Text style={styles.error}>
          {this.state.error}
				</Text>
        </View>
      );
    }
  }