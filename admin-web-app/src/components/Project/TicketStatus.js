import React, { Component } from 'react';
import { View, Button, TextInput } from 'react-native';

export default class TicketStatus extends Component {
	render() {
		if(this.props.state.value === 'FINISHED'){
			return (
				<button  style={{color: '#5daedb'}}>
					FINISHED
				</button>
			);
		} else {
			return (
				<button  style={{color: '#A25124'}}>
					OPEN
				</button>
			);
		}
	}
}
