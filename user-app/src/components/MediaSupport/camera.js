import React, {Component} from 'react';
import {Text, View} from 'react-native';
import styles from '../Login/Design';
import Camera from 'react-native-camera';

export default class CameraAccess extends Component { 
    
render() {
return ( 
        <View style={styles.container}>
        <Camera 
         ref={(cam) => {
        this.camera = cam
             }}
                     style={styles.view}
       aspect={Camera.constants.Aspect.fill}>
        <Text style={styles.capture} onPress={this.takePicture.bind(this)}>[CAPTURE]</Text>
       </Camera>
         </View>
       
   );
 }

  takePicture() {
    const options = {};
     this.camera.capture({metadata: options}).then((data) => console.log(data))
   .catch(err => console.error(err));
  }
}