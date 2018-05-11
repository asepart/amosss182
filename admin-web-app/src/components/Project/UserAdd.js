import React, {Component} from 'react';
import {Button,TextInput,ActivityIndicator,View} from 'react-native';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';

export default class UserAdd extends Component {
	constructor(props) {
		super(props);
		this.state = {
			userName: 'UselessEmployee',
			phone:	'+49123456789'
		};
	}

	showProjectList () {
		setState({
			isAuth: true,
			show: '',
			param: ''
		});
	}

	showUserList () {
		setState({
			isAuth: true,
			show: 'listUsers',
			param: this.props.project
		});
	}

	putProject() {
		fetch(URL + '/projects/' + this.props.project + '/users/' + this.state.userName, {
				method: 'PUT',
				headers: getAuth()
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					projectName: "",
					entryKey: ""
				}, function() {});
			})
			.catch((error) => {
				console.error(error);
			});
		this.showProjectList ();
	}

	addUser(){
		fetch(URL + '/projects/' + this.props.project + '/users/' + this.state.phone, {
				method: 'POST',
				headers: getAuth()
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					projectName: "",
					entryKey: ""
				}, function() {});
			})
			.catch((error) => {
				console.error(error);
			});
		this.showUserList ();
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style = {{flex: 1, padding: 20}}>
					<ActivityIndicator / >
				</View>
			)
		}
		if (this.props.project === ''){
			return(
				<View>
					<TextInput
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({userName: text})}
						value = {this.state.userName}
					/>
					<Button onPress = { this.putProject.bind(this) } title = "Add" color = "#841584" />
					<Button onPress = { this.showProjectList } title = "Cancel" color = "#841584" />
				</View>
			);
		}
		return (
			<View>
				<TextInput
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({phone: text})}
					value = {this.state.phone}
				/>
				<Button onPress = { this.addUser.bind(this) } title = "Add" color = "#841584" />
				<Button onPress = { this.showProjectList } title = "Cancel" color = "#841584" />
			</View>
		);
	}
}
