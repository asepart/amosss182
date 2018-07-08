import React, { Component } from 'react';
import CPopup from '../shared/CPopup';

export class ChatVideo extends Component {
	render() {
		return (
			<CPopup toggle={this.props.src + '?thumbnail=true'}>
				<video width="320" height="240" controls>
					<source src={this.props.src} type="video/mp4"/>
					Your browser does not support the video tag.
				</video>
			</CPopup>
		);

	}
}
