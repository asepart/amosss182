import React, {Component} from 'react';
import {Button,TextInput,View,Picker} from 'react-native';
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';
import Popup from "reactjs-popup";

var pickerPlaceholder = "Category";

export default class TicketCreate extends Component {

	constructor(props) {
    super(props);
    this.state = {
			open: false,
			ticketName: '',
			ticketSummary: '',
			ticketDescription: '',
			ticketCategory: '',
			requiredObservations: '',
			id: '0',
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
		fetch(URL + '/projects/' + this.props.project + '/tickets/', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({id: this.state.id, ticketName: this.state.ticketName, ticketSummary: this.state.ticketSummary, ticketDescription: this.state.ticketDescription, ticketCategory: this.state.ticketCategory, requiredObservations: this.state.requiredObservations})
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					ticketName: "",
					ticketSummary: "",
					ticketDescription: "",
					ticketCategory: "",
					requiredObservations: "",
					id: ""
				}, function() {});
			})
			.catch((error) => {
				console.error(error);
			});
		this.setState({
		  open: false
		})
	}

	render() {
		var buttonEnabled = (this.state.ticketName !== '' && this.state.ticketSummary !== '' && this.state.ticketDescription !== '' && this.state.ticketCategory !== pickerPlaceholder && this.state.requiredObservations !== '');

		return (	// TODO: add edit icon instead of text here
			<div>
				<button onClick={this.openPopup} style={{color: '#5daedb'}}>
					ADD TICKET
				</button>
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
						onChangeText = {(text) => this.setState({ticketName: text})}
						value = {this.state.ticketName}
					/>
					<TextInput
						placeholder = "Summary"
						textAlign={'center'}
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({ticketSummary: text})}
						value = {this.state.ticketSummary}
					/>
					<TextInput
						placeholder = "Description"
						multiline={true}
						style = {{height: 600, borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({ticketDescription: text})}
						value = {this.state.ticketDescription}
					/>
					<Picker
						style = {{height: 40, backgroundColor: 'transparent', borderColor: 'gray', borderWidth: 1, textAlign: 'center'}}
						onValueChange = {(text) => this.setState({ticketCategory: text})}
						selectedValue = {this.state.ticketCategory}
					>
						<Picker.Item label = {pickerPlaceholder} value = {pickerPlaceholder} />
						<Picker.Item label = "ONE_TIME_ERROR" value = "ONE_TIME_ERROR" />
						<Picker.Item label = "TRACE" value = "TRACE" />
						<Picker.Item label = "BEHAVIOR" value = "BEHAVIOR" />
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
