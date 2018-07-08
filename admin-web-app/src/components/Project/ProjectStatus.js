import React, { Component } from 'react';
import { Text } from 'react-native';

export default class ProjectStatus extends Component {
	render() {
		if(this.props.state.value === "true"){
			return (
				<Text  style={{color: '#5daedb'}}>
					FINISHED
				</Text>
			);
		} else {
			return (
				<Text  style={{color: '#A25124'}}>
					OPEN
				</Text>
			);
		}
	}
}
