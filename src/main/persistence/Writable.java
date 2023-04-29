
package persistence;

import org.json.JSONObject;

// Code taken from CPSC 210 JSONSerialization Demo
public interface Writable {
    //EFFECTS: returns this as JSON object;
    JSONObject toJson();
}


