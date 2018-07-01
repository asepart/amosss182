import React, {Component} from 'react';
import {Button,TextInput,View} from 'react-native';
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';
import Popup from "reactjs-popup";
import {setUpdateBoolean} from '../shared/GlobalState';
import { Link } from 'react-router-dom';

export default class UserAdd extends Component {

	constructor(props) {
		super(props);
		this.state = {
			open: false,
			loginName: '',
			password: '',
			firstName: '',
			lastName: '',
			phoneNumber: ''
		};
	}
	openPopup = () => {
		this.setState({ open: true });
	};
	closePopup = () => {
		this.setState({
		 	open: false,
			loginName: '',
			password: '',
			firstName: '',
			lastName: '',
			phoneNumber: ''
		})
	};

	addUser(){
		let auth = getAuthForPost();
		fetch(URL + '/users', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({loginName: this.state.loginName, password: this.state.password, firstName: this.state.firstName, lastName: this.state.lastName, phoneNumber: this.state.phoneNumber})
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({}, function() {});
			})
			.catch((error) => {
				console.error(error);
			});

		this.props.callToParent();
		setUpdateBoolean(true);
		this.closePopup();
	}

	render() {
		var buttonEnabled = (this.state.loginName !== '' && this.state.password !== '' && this.state.firstName !== '' && this.state.lastName !== '' && this.state.phoneNumber !== '');

		return(
			<div>
				<div>
					{window.location.href.indexOf("usermanagement") !== -1 ? (
						<Link to = "/usermanagement" style={{textDecoration: 'none'}}>
							<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/add.png')} alt=""/>
						</Link>
					) : (
						<Link to = {"/projects/" + this.props.project + "/users"} style={{textDecoration: 'none'}}>
							<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/add.png')} alt=""/>
						</Link>
					)}
				</div>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
				<View>
					<TextInput
						placeholder = "Login Name"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({loginName: text})}
						value = {this.state.loginName}
					/>
					<TextInput
						placeholder = "Password"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({password: text})}
						value = {this.state.password}
					/>
					<TextInput
						placeholder = "Given Name"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({firstName: text})}
						value = {this.state.firstName}
					/>
					<TextInput
						placeholder = "Surname"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({lastName: text})}
						value = {this.state.lastName}
					/>
					<TextInput
						placeholder = "Phone Number"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({phoneNumber: text})}
						value = {this.state.phoneNumber}
					/>
					<Button onPress = { this.addUser.bind(this) } title = "Add" color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
				</View>
				</Popup>
			</div>
		);
	}
}
