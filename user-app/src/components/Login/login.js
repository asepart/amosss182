import React, {Component} from 'react';
import {
	Text,
	View,
	TextInput,
	TouchableOpacity,
	StyleSheet
} from 'react-native';

//Navigation library (need to add to BoM)
import {
	StackNavigator,
  } from 'react-navigation';

 //Design template 
import styles from './Design';

import {setUsername, setPSW, authenticate} from './auth';
import {setState} from './state';

class Login extends Component {

	//setting page title 
	static navigationOptions= {
		title: 'Login',
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
			email: "",
			password: "",
			error: "",
			showProgress: false
		}
	}

	
	 async onLoginPressed() {

		
		this.setState({showProgress: true})
		setUsername(this.state.email);
		setPSW(this.state.password);

		if(authenticate()){
		setState({isAuth: true});

		//navigate to different site
		const { navigate } = this.props.navigation;
		navigate("Second", { name: "SecondScreen" })	
		}
	}


	render() {
		return (<View style={styles.container}>

			<TextInput  onChangeText={(val) => this.setState({email: val})} placeholder="email" placeholderTextColor="#FFF" style={styles.input}/>
			<TextInput onChangeText={(val) => this.setState({password: val})} placeholder="password" placeholderTextColor="#FFF"  secureTextEntry style={styles.input}/>
			<TouchableOpacity onPress={this.onLoginPressed.bind(this)} style={styles.buttonContainer}>
			
				<Text style={styles.buttonText}>LOGIN</Text>

			</TouchableOpacity>

		</View>);
	}
}

export default Login;

