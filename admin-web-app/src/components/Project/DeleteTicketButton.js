import React, { Component } from 'react';
import { View, Button } from 'react-native';
import Popup from "reactjs-popup";
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import {setUpdateBoolean} from '../shared/GlobalState';

export default class DeleteTicketButton extends Component {

	constructor(props) {
		super(props);
		this.state = { open: false };
	}
	openPopup = () => {
		this.setState({ open: true });
	};
	closePopup = () => {
		this.setState({ open: false });
	};

	deleteTicket() {
		fetch(URL + '/tickets/' + this.props.proj.row.id, {method:'DELETE', headers: getAuth()})
			.then((response) => response.json())
			.catch((error) => {
				console.error(error);
			});

		this.props.callToParent();
		setUpdateBoolean(true);
		this.setState({
				open: false
		})
	}

	render() {
		return (
			<div>
				<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/delete.png')} alt=""/>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
					<View>
						<Button
							onPress = { function doNothing() {} }
							disabled = {true}
							title = {"Delete " + this.props.proj.row.name + " from " + this.props.nameFromParent + "?"}
						/>
						<Button
							onPress = { this.deleteTicket.bind(this) }
							title = "Delete"
							color = "#0c3868"/>
						<Button
							onPress = { this.closePopup }
							title = "Cancel"
							color = "#0e4a80"
						/>
					</View>
			</Popup>
		</div>
	);
	}
}
