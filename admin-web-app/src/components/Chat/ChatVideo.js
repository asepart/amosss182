import React, { Component } from 'react';
import CPopup from '../shared/Popup';

export default class ChatVideo extends Component {
	render() {
		return (
			<CPopup toggle="<img source={this.props.children.video + '?thumbnail=true'} style={{width: 100, height: 100}}/>">
				<video width="320" height="240" controls>
					<source src={this.props.children.video} type="video/mp4" />
					Your browser does not support the video tag.
				</video>
			</CPopup>
		);
	}
}
