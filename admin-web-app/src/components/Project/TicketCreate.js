import React, {Component} from 'react';
import {Button,TextInput,View,Picker} from 'react-native';
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';
import Popup from "reactjs-popup";
import {setUpdateBoolean} from '../shared/GlobalState';

var pickerPlaceholder = "Category";

export default class TicketCreate extends Component {

	constructor(props) {
		super(props);
		this.state = {
			open: false,
			name: '',
			summary: '',
			description: '',
			category: '',
			requiredObservations: '',
			projectKey: this.props.project,
		};
	}
	openPopup = () => {
		this.setState({ open: true });
	};
	closePopup = () => {
		this.setState({ open: false });
	};

	createTicket() {
		let auth = getAuthForPost();
		fetch(URL + '/tickets/', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({name: this.state.name, summary: this.state.summary, description: this.state.description, category: this.state.category, requiredObservations: this.state.requiredObservations, projectKey: this.state.projectKey})
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
			open: false,
			name: '',
			summary: '',
			description: '',
			category: '',
			requiredObservations: '',
		})
	}

	render() {
		var buttonEnabled = (this.state.name !== '' && this.state.summary !== '' && this.state.description !== '' && this.state.category !== pickerPlaceholder && this.state.requiredObservations !== '');

		return (
			<div>
				<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/add.png')} alt=""/>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
				<View>
					<TextInput
						placeholder = "Name"
						textAlign={'center'}
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({name: text})}
						value = {this.state.name}
					/>
					<TextInput
						placeholder = "Summary"
						textAlign={'center'}
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({summary: text})}
						value = {this.state.summary}
					/>
					<TextInput
						placeholder = "Description"
						multiline={true}
						style = {{height: 600, borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({description: text})}
						value = {this.state.description}
					/>
					<Picker
						style = {{height: 40, backgroundColor: 'transparent', borderColor: 'gray', borderWidth: 1}}
						onValueChange = {(text) => this.setState({category: text})}
						selectedValue = {this.state.category}
					>
						<Picker.Item label = {pickerPlaceholder} value = {pickerPlaceholder} />
						<Picker.Item label = "ONE_TIME_ERROR" value = "one-time-error" />
						<Picker.Item label = "TRACE" value = "trace" />
						<Picker.Item label = "BEHAVIOR" value = "behavior" />
					</Picker>
					<TextInput
						placeholder = "Required Observations"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({requiredObservations: text})}
						value = {this.state.requiredObservations}
					/>
					<Button onPress = { this.createTicket.bind(this) } title = "Add" color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
				</View>
				</Popup>
			</div>
		);
	}
}
