import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity} from 'react-native';
import styles from './Design';
import {setState} from './state';
import {URL} from './const';
import {username, psw, setPSW, getAuth, getAuthForPost} from './auth';

export default class UserInfo extends Component {
    
	static navigationOptions = {
		title: 'User Information',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
    } 

    constructor(props) {
		super(props);
		this.state = {
			loginName: username,
			password: '',
			newPassword: '',
			newPasswordConfirm: '',
			firstName: '',
			lastName: '',
			phoneNumber: ''
		};
	}

    async componentDidMount() {
        await this.fetchUserInformation();
    }

    async onPressUpdate() {
    	if(this.state.password !== psw) {
    		alert('Wrong password. Please enter your password again.');
    		return;
    	}
    	if(this.state.newPassword !== this.state.newPasswordConfirm) {
    		alert('The new password does not match the confirmation. Please enter your new password again.');
    		return;
    	}
    	this.updateUser();
    }
    
    updateUser() {
    	let auth = getAuthForPost();
    	fetch(URL + '/users', {
    		method:'POST',
    		headers: auth,
    		body: JSON.stringify({loginName: this.state.loginName, password: this.state.newPassword, firstName: this.state.firstName, lastName: this.state.lastName, phoneNumber: this.state.phoneNumber})
    	}).then((response) => {
    		if(this.state.newPassword !== '') {
    			setPSW(this.state.newPassword);
    		}
    		alert('Updated user information.');
    	}).catch((error) => {
			console.error(error);
			alert('Something went wrong.')
    	});
    }
     
    async fetchUserInformation() {
        await fetch(URL + '/users' + '/' + this.state.loginName, {
        		method:'GET',
        		headers: getAuth()
        }).then((response) => response.json())
        .then((responseJson) => {
				this.setState({
					firstName: responseJson.firstName,
					lastName: responseJson.lastName,
					phoneNumber: responseJson.phoneNumber
				}, function() {});
		}).catch((error) => {
				console.error(error);
		});
    }
      
    render() {
    	
    	var buttonEnabled = (this.state.password !== '' && this.state.firstName !== '' && this.state.lastName !== '' && this.state.phoneNumber !== '');
    	
    	return (
    		   <View style={styles.containerAlign}>
    		   		<TextInput
    		   			onChangeText={(text) => this.setState({firstName: text})}
    		   			value={this.state.firstName}
    		   			placeholder="Given Name" placeholderTextColor="#FFF"
    		   			underlineColorAndroid="transparent" style={styles.input}
    		   		/>
    		   		<TextInput
    		   			onChangeText={(text) => this.setState({lastName: text})}
		   				value={this.state.lastName}
    		   			placeholder="Surname" placeholderTextColor="#FFF"
    		   			underlineColorAndroid="transparent" style={styles.input}
    		   		/>
    		   		<TextInput
    		   			onChangeText={(text) => this.setState({phoneNumber: text})}
		   				value={this.state.phoneNumber}
    		   			placeholder="Phone Number" placeholderTextColor="#FFF"
    		   			underlineColorAndroid="transparent" style={styles.input}
    		   		/>
    		   		
    		   		<View style={{ height: 50 }} />
    		   		
    		   		<TextInput
    		   			onChangeText={(text) => this.setState({newPassword: text})}
    		   			placeholder="New Password" placeholderTextColor="#FFF"
    		   			underlineColorAndroid="transparent" style={styles.input}
    		   			secureTextEntry={true}
    		   		/>
    		   		<TextInput
		   				onChangeText={(text) => this.setState({newPasswordConfirm: text})}
		   				placeholder="Confirm" placeholderTextColor="#FFF"
		   				underlineColorAndroid="transparent" style={styles.input}
    		   			secureTextEntry={true}
    		   		/>
    		   		
    		   		<View style={{ height: 50 }} />
    		   		
    		   		<TextInput
		   				onChangeText={(text) => this.setState({password: text})}
		   				placeholder="Current Password * " placeholderTextColor="#FFF"
		   				underlineColorAndroid="transparent" style={styles.input}
    		   			secureTextEntry={true}
    		   		/>
    		   		<TouchableOpacity
    		   			onPress={this.onPressUpdate.bind(this)}
    		   			disabled={!buttonEnabled}
    		   			style={styles.buttonContainer}>
    		   			<Text style={styles.buttonText}>Update</Text>
    		   		</TouchableOpacity>
    		   	</View>
   	   );
    }
}