import React, {Component} from 'react';
import '../../index.css';
import Popup from "reactjs-popup";


export default class CPopup extends Component {
	render () {
		return(
			<Popup
				trigger={<img src={this.props.toggle} alt="Show Media" style={{width: 100, height: 100}}/>}
				closeOnDocumentClick
				contentStyle= {{
					position: 'sticky',
				}}
			>
				{this.props.children}
			</Popup>
		)
	}
}
