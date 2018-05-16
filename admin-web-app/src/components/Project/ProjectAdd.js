import React, {Component} from 'react';
import {Button,TextInput,ActivityIndicator,View} from 'react-native';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';

export default class ProjectAdd extends Component {
	constructor(props) {
		super(props);
		this.state = {
			projectName: 'Useless Project',
			entryKey: '7C2310F49B45203BF5E4DDC2A12C94DA'
		};
	}

	showProjectList () {
		setState({
			isAuth: true,
			show: '',
			param: ''
		});
	}

	async putProject() {
		let auth = getAuth();
		await fetch(URL + '/projects/' + this.state.projectName, {
				method: 'PUT',
				headers: auth,
				body: this.state.entryKey
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

	render() {
		if (this.state.isLoading) {
			return (
				<View style = {{flex: 1, padding: 20}}>
					<ActivityIndicator / >
				</View>
			)
		}
		return (
			<View>
			<TextInput
				style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
				onChangeText = {(text) => this.setState({projectName: text})}
				value = {this.state.projectName}
			/>
			<TextInput
				style = {{height: 40,width: '25em',borderColor: 'gray',borderWidth: 1}}
				onChangeText = { (text) => this.setState({entryKey: text})}
				value = { this.state.entryKey }
			/>
			<Button onPress = { this.putProject.bind(this) } title = "Add" color = "#0c3868" />
			<Button onPress = { this.showProjectList } title = "Cancel" color = "#0e4a80" />
			</View>
		);
	}
}
