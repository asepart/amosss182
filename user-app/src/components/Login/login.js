import React, {Component} from 'react';
import {
	Text,
	View,
	TextInput,
	TouchableOpacity,
	StyleSheet
} from 'react-native';

//Navigation library 
import {
	StackNavigator,
  } from 'react-navigation';

 //Design template 
import styles from './Design';

import {setUsername, setPSW, isAuth} from './auth';
import {setState} from './state';

export default class Login extends Component {

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
		}
	}

	
	 async onLoginPressed() {

		setUsername(this.state.email);
		setPSW(this.state.password);

		if(await isAuth()){
			setState({isAuth: true});

			//navigate to different site
		const { navigate } = this.props.navigation;
		navigate("Second", { name: "Projects" })

		} else {
			this.setState({error: "Invalid credentials!"});
		}
	
		
	
	}


	render() {
		return (<View style={styles.containerAlign}>

			<TextInput  onChangeText={(text) => this.setState({email: text})} placeholder="username" placeholderTextColor="#FFF" style={styles.input}/>
			<TextInput onChangeText={(text) => this.setState({password: text})} placeholder="password" placeholderTextColor="#FFF"  secureTextEntry style={styles.input}/>
			<TouchableOpacity onPress={this.onLoginPressed.bind(this)} style={styles.buttonContainer}>
			
				<Text style={styles.buttonText}>LOGIN</Text>

			</TouchableOpacity>

			<Text style={styles.error}>
					{this.state.error}
				</Text>
		</View>);
	}
}

