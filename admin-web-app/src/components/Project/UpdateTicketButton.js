import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';

export default class UpdateTicketButton extends Component {
	displayTicket() {
		setState({
			isAuth: true,
			show: 'createTicket',
			param: this.props.project,
			name: this.props.name,
			tName: this.props.tick.row.ticketName,
			tSummary: this.props.tick.row.ticketSummary,
			tDescription: this.props.tick.row.ticketDescription,
			tCategory: this.props.tick.row.ticketCategory,
			tRequiredObservations: this.props.tick.row.requiredObservations,
			tId: this.props.tick.row.id
		});
	}
	render() {
		return (	// TODO: add edit icon instead of text here
			<Text
				onPress = { this.displayTicket.bind(this) }
				style={{color: '#5daedb'}}
			>
				EDIT
			</Text> 
		);
	}
}
