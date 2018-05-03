import React, {Component} from 'react';
import {
	StyleSheet,
	Text,
	View,
	TextInput,
	TouchableOpacity,
} from 'react-native';
import {authenticate, setPSW, setUsername} from '../shared/auth';
import {setState} from '../shared/GlobalState';

class Login extends Component {
	constructor(props) {
		super(props)

		this.state = {
			email: "",
			password: "",
			error: "",
			showProgress: false
		}
	}

	async onLoginPressed() {
		this.setState({showProgress: true});
		setUsername(this.state.email);
		setPSW(this.state.password);

		if(authenticate()){
			setState({isAuth: true});

		}
	}

	render() {
		return (<View style={styles.container}>

			<TextInput onChangeText={(text) => this.setState({email: text})} placeholder="email" placeholderTextColor="#FFF" style={styles.input}/>
			<TextInput onChangeText={(text) => this.setState({password: text})} placeholder="password" placeholderTextColor="#FFF" secureTextEntry="secureTextEntry" style={styles.input}/>
			<TouchableOpacity onPress={this.onLoginPressed.bind(this)} style={styles.buttonContainer}>

				<Text style={styles.buttonText}>LOGIN</Text>

			</TouchableOpacity>

			<Text style={styles.error}>
				{this.state.error}
			</Text>

		</View>);
	}
}

export default Login;

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
