import React, { Component } from 'react';
import { Text } from 'react-native';

export default class ChatMessage extends Component {
	render() {
		if(this.props.type === 'img'){
			//Image
		} else if(this.props.type === 'vid'){
			//Video
		} else if(this.props.type === 'aud'){
			//Audio
		} else {
			return (
				<Text>
					{this.props.children}
				</Text>
			);
		}
	}
}
