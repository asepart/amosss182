import React, { Component } from 'react';
import { Text } from 'react-native';

export default class TicketStatus extends Component {
	render() {
		if(this.props.state === 'FINISHED'){
			return (
				<Text  style={{color: '#5daedb'}}>
					FINISHED
				</Text>
			);
		} else {
			return (
				<Text  style={{color: '#A25124'}}>
					Open
				</Text>
			);
		}
	}
}
