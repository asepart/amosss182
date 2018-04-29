import React, { Component } from 'react';
//import logo from './logo.svg';
//import './App.css';
import {
  StyleSheet,
  Text,
  View,
  TextInput,
  TouchableOpacity
} from 'react-native';

class Login extends Component {
  render() {
    return (
      <View style={styles.container}>
      <TextInput
      placeholder="username or email"
      placeholderTextColor="#FFF"
      style={styles.input}
      />
      <TextInput
      placeholder="password"
      placeholderTextColor="#FFF"
      secureTextEntry
      style={styles.input}
      />
      <TouchableOpacity style={styles.buttonContainer}>
       
        <Text style={styles.buttonText}>LOGIN</Text>

      </TouchableOpacity>
      </View>
    );
  }
}

export default Login;

const styles = StyleSheet.create( {
  container: {
    flex: 1,
    padding: 20,
    //alignItems moves items to upper center
    alignItems: 'center',
    //justifyContent moves items to center of page
    justifyContent: 'center'
    
    
  }, 
  input: {
    height: 40,
    width: 200,
    backgroundColor: '#a4a4a4',
    marginBottom: 10,
    color: '#FFF',
    paddingHorizontal: 10,
    opacity: '0.8'
    
  },

  buttonContainer: {
    backgroundColor: '#2980b9',
    paddingVertical: 15,
    width: 200
  },
  buttonText: {
    textAlign: 'center',
    color: '#FFF',
    fontWeight: '700'

  }
});
