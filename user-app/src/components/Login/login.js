import React, {Component} from 'react';
import {
	Text,
	View,
	TextInput,
	TouchableOpacity,
	StyleSheet,
	Image
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
			backgroundColor:'#5daedb'
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
			info: "",
			infoType: {}
		}
	}

	
	 async onLoginPressed() {

		setUsername(this.state.email);
		setPSW(this.state.password);

		if(await isAuth()){
			setState({isAuth: true});

		this.setState({info: "Valid credentials", infoType: styles.success});

		//navigate to different site
		const { navigate } = this.props.navigation;

		navigate("Tenth", { name: "ProjectList" })


		} else {
			this.setState({info: "Invalid credentials", infoType: styles.error});
		}
	
		
	
	}


	render() {
	var buttonEnabled = (this.state.email !== '' && this.state.password !== '');
		return (<View style={styles.containerAlign}>
			<Image source={require('../images/icon.png')} style={styles.icon} />
			<TextInput  onChangeText={(text) => this.setState({email: text})} placeholder="username" placeholderTextColor="#FFF" underlineColorAndroid="transparent" style={styles.input}/>
			<TextInput onChangeText={(text) => this.setState({password: text})} placeholder="password" placeholderTextColor="#FFF" underlineColorAndroid="transparent"  secureTextEntry style={styles.input}/>
			<TouchableOpacity disabled={!buttonEnabled} onPress={this.onLoginPressed.bind(this)} style={styles.buttonContainer}>
			
				<Text style={styles.buttonText}>LOGIN</Text>

			</TouchableOpacity>

			<Text style={this.state.infoType}>
					{this.state.info}
				</Text>
		</View>);
	}
}
