import React, { Component } from 'react';
import { View, Text, Button, TextInput, Picker } from 'react-native';
import Popup from "reactjs-popup";
import {getAuth, getAuthForPost, getAuthForMediaPost} from '../shared/auth';
import {URL, FileSelector} from '../shared/const';
import '../../index.css';
import {setUpdateBoolean} from '../shared/GlobalState';
import { Link } from 'react-router-dom';
import { File } from './File';

var pickerPlaceholder = "Category";

export default class UpdateTicketButton extends Component {

	constructor(props) {
		super(props);
		this.state = {
			open: false,
			name: '',
			summary: '',
			description: '',
			category: '',
			requiredObservations: '',
			id: '',
			newFile: null,
			files: null
		};
	}
	openPopup = () => {
		this.setState({ open: true });
		this.getVars();
	};
	closePopup = () => {
		this.setState({ open: false });
	};

	//needed to get right row values after changes in parent component
	getVars() {
		this.setState({
			name: this.props.tick.row.name,
			summary: this.props.tick.row.summary,
			description: this.props.tick.row.description,
			category: this.props.tick.row.category,
			requiredObservations: this.props.tick.row.requiredObservations,
			id: this.props.tick.row.id
		})
	}

	createTicket() {
		let auth = getAuthForPost();
		fetch(URL + '/tickets/', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({id: this.state.id, name: this.state.name, summary: this.state.summary, description: this.state.description, category: this.state.category, requiredObservations: this.state.requiredObservations, projectKey: this.props.project})
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
		this.setState({
		  open: false
		})
	}

	handleFile(selectorFiles: FileList) {
		var files = selectorFiles;
		const formData = new FormData();
		formData.append('file', files[0]);
		var fileID = '';

		fetch(URL + '/files/' + this.state.id, {
			method:'POST',
			headers: getAuthForMediaPost(),
			body: formData,
		})
		.then(response => {
			fileID = response.json();
		})
		.catch((error) => {
			console.log(error);
		});

		fetch(URL + '/tickets/' + this.state.id + '/attachments', {
			method:'POST',
			headers: getAuth(),
			body: fileID,
		})
		.catch((error) => {
			console.log(error);
		});
		this.closePopup();
	}

	deleteFile(name) {
		alert(name); return;

		var files = this.state.files;
		var index = files.indexOf(name);
		if (index > -1) {
		  files.splice(index, 1);
		}
		this.setState({files: files});

		//TODO: one could delete the file from the server
	}

	listFiles () {
		if(this.state.files === null) {
			//return (<Text>No files uploaded</Text>);
			return(<View><File name="foo"/></View>)
		} else {
			return this.state.files.map(file => {
				return (
					<View>
						<Text>{file}</Text>
						<img src={require('../images/delete.png')} alt="delete" onClick={this.deleteFile(file).bind(this)}/>
					</View>
				);
			});
		}
	}

	render() {
		var buttonEnabled = (this.state.name !== '' && this.state.summary !== '' && this.state.description !== '' && this.state.category !== pickerPlaceholder && this.state.requiredObservations !== '');

		return (
			<div>
				<Link to = {"/projects/" + this.props.project} style={{textDecoration: 'none'}}>
					<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/edit.png')} alt=""/>
				</Link>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
				<View>
					<TextInput
						placeholder = "Name"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({name: text})}
						value = {this.state.name}
					/>
					<TextInput
						placeholder = "Summary"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({summary: text})}
						value = {this.state.summary}
					/>
					<TextInput
						placeholder = "Description"
						multiline={true}
						style = {{height: window.innerHeight*0.4, borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({description: text})}
						value = {this.state.description}
					/>
					<Picker
						style = {{height: 40, backgroundColor: 'transparent', borderColor: 'gray', borderWidth: 1, textAlign: 'center'}}
						onValueChange = {(text) => this.setState({category: text})}
						selectedValue = {this.state.category}
					>
						<Picker.Item label = {pickerPlaceholder} value = {pickerPlaceholder} />
						<Picker.Item label = "one-time-error" value = "one-time-error" />
						<Picker.Item label = "trace" value = "trace" />
						<Picker.Item label = "behavior" value = "behavior" />
					</Picker>
					<TextInput
						placeholder = "Required Observations"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({requiredObservations: text})}
						value = {`${this.state.requiredObservations}`}
					/>
					{this.listFiles()}
					<FileSelector
						onLoadFile = {(files:FileList) => this.handleFile(files)}
					/>
					<Button onPress = { this.createTicket.bind(this) } title = "Update" color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
				</View>
				</Popup>
			</div>
		);
	}
}
