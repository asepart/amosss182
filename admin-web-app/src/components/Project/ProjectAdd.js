import React, {Component} from 'react';
import {Button,TextInput,ActivityIndicator,View,Text} from 'react-native';
import {getAuthForPost, username} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';

var button = "Add";
var editKey = true;

export default class ProjectAdd extends Component {
	
	constructor(props) {
		super(props);
		this.state = {
			projectName: this.props.name,
			entryKey: this.props.project,
			owner: username
		};
		if(this.state.entryKey !== '') {
			button = "Update";
			editKey = false;
		} else {
			button = "Add";
			editKey = true;
		}
	}

	showProjectList () {
		setState({
			isAuth: true,
			show: '',
			param: ''
		});
	}

	async putProject() {
		let auth = getAuthForPost();
		await fetch(URL + '/projects', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({projectName: this.state.projectName, entryKey: this.state.entryKey, owner: this.state.owner})
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					projectName: "",
					entryKey: "",
					owner: ""
				}, function() {});
			})
			.catch((error) => {
				console.error(error);
			});
		this.showProjectList ();
	}

	render() {
		var buttonEnabled = (this.state.entryKey !== '' && this.state.projectName !== '');
		if (this.state.isLoading) {
			return (
				<View style = {{flex: 1, padding: 20}}>
					<ActivityIndicator / >
				</View>
			)
		}
		return (	// TODO: add home icon instead of text here
			<View>
				<Text
					onPress = { this.showProjectList.bind(this) }
					style={{color: '#5daedb'}}
				>
					HOME
				</Text> 
			<TextInput
				placeholder = "Name"
				style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
				onChangeText = {(text) => this.setState({projectName: text})}
				value = {this.state.projectName}
			/>
			<TextInput
				placeholder = "Entry Code"
				style = {{height: 40,width: '25em',borderColor: 'gray',borderWidth: 1}}
				onChangeText = { (text) => this.setState({entryKey: text})}
				value = { this.state.entryKey }
				editable = {editKey}
			/>
			<Button onPress = { this.putProject.bind(this) } title = {button} color = "#0c3868" disabled = {!buttonEnabled}/>
			<Button onPress = { this.showProjectList } title = "Cancel" color = "#0e4a80" />
			</View>
		);
	}
}
