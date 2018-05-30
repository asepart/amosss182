import React, {Component} from 'react';
import {
	StyleSheet,
	Text,
	View,
	TextInput,
	TouchableOpacity,
	Image
} from 'react-native';
import {isAuth, setPSW, setUsername} from '../shared/auth';
import {setState} from '../shared/GlobalState';

export default class Login extends Component {
	constructor(props) {
		super(props)

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
		} else {
			this.setState({error: "Invalid credentials!"});
		}
	}

	handleKeyPress = (event) => {
	  if(event.key == 'Enter'){
	    this.onLoginPressed();
	  }
	}

	render() {
		return (
			<View style={styles.container}>
				<img src={require('../images/icon.png')}/><p/>
				<TextInput onKeyPress={this.handleKeyPress} onChangeText={(text) => this.setState({email: text})} placeholder="username" placeholderTextColor="#FFF" style={styles.input}/>
				<TextInput onKeyPress={this.handleKeyPress} onChangeText={(text) => this.setState({password: text})} placeholder="password" placeholderTextColor="#FFF" secureTextEntry={true} style={styles.input}/>
				<TouchableOpacity onPress={this.onLoginPressed.bind(this)} style={styles.buttonContainer}>
					<Text style={styles.buttonText}>LOGIN</Text>
				</TouchableOpacity>
				<Text style={styles.error}>
					{this.state.error}
				</Text>
			</View>
		);
	}
}

const styles = StyleSheet.create({
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
		paddingHorizontal: 10
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

	},
	error: {
		color: 'red',
		paddingTop: 10
	}
});
