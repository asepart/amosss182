import React, { Component } from 'react';
import {ActivityIndicator,Button, View} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import ProjectButton from './ProjectButton';
import ProjectAdd from './ProjectAdd';
import UpdateProjectButton from './UpdateProjectButton';
import DeleteProjectButton from './DeleteProjectButton';
import 'react-table/react-table.css';
import '../../index.css';
import {Link} from 'react-router-dom'

export default class ProjectList extends Component {

	constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
	}

	componentDidMount() {
		this.fetchData();
	}
	
	componentDidUpdate() {
		this.fetchData();
	}

	fetchData() {
		return fetch(URL + '/projects', {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				dataSource: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		const columns = [
			{
				Header: 'Name',
				accessor: 'projectName',
				Cell: props => <ProjectButton proj={props}/>,
				Footer: props => <ProjectAdd project={this.state.param} name={this.state.name}/>
			}, {
				Header: 'Entry Code',
				accessor: 'entryKey' // String-based value accessors!
			}, {
				Header: '',
				accessor: '',
				maxWidth: 55,
				Cell: props => <UpdateProjectButton proj={props}/>
			}, {
				Header: '',
				accessor: '',
				maxWidth: 75,
				Cell: props => <DeleteProjectButton proj={props}/>
			}
		]

		return (
			<View>
				<View style={{flexDirection: 'row'}}>
					<View style={{flex:1}}>
						<Button
							onPress = { function doNothing() {} }
							disabled = {true}
							title = {"Projects"}
						/>
					</View>
					<View style={{flex:1}}>
						<Link to="/usermanagement" style={{textDecoration: 'none'}}>
						<Button
							title = "Users"
							color = "#0e4a80"
						/>
						</Link>
					</View>
				</View>
				<ReactTable
					data={this.state.dataSource}
					noDataText="No Projects found!"
					minRows={this.state.dataSource.length}
					showPagination={false}
					columns={columns}
				/>
			</View>
		);
	}
}
