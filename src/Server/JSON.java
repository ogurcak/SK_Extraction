
package Server;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;






public class JSON
{



	public JSONArray getNamesInJSON(List<String> names) throws JSONException {

		JSONArray namesArray = new JSONArray();
		if (names.size() > 0)
			for (String name : names) {
				if (namesArray.length() >= 5)
					break;
				boolean equal = false;
				for (int i = 0; i < namesArray.length(); i++)
					if (namesArray.getString(i).equalsIgnoreCase(name))
						equal = true;
				if (!equal)
					namesArray.put(name);
			}
		else
			namesArray.put("");

		return namesArray;
	}






	public JSONArray getPlacesInJSON(List<String> places) throws JSONException {

		JSONArray placesArray = new JSONArray();
		if (places.size() > 0)
			for (String place : places) {
				if (placesArray.length() >= 5)
					break;
				boolean equal = false;
				for (int i = 0; i < placesArray.length(); i++)
					if (placesArray.getString(i).equalsIgnoreCase(place))
						equal = true;
				if (!equal)
					placesArray.put(place);
			}

		else
			placesArray.put("");

		return placesArray;
	}






	public JSONArray getDatesFromInJSON(List<Calendar> datesFrom) throws JSONException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		JSONArray datesFromArray = new JSONArray();
		if (datesFrom.size() > 0)
			for (Calendar time : datesFrom) {
				if (datesFromArray.length() >= 5)
					break;
				boolean equal = false;
				for (int i = 0; i < datesFromArray.length(); i++)
					if (datesFromArray.getString(i).equalsIgnoreCase(sdf.format(time.getTime())))
						equal = true;
				if (!equal)
					datesFromArray.put(sdf.format(time.getTime()));
			}
		else
			datesFromArray.put("");

		return datesFromArray;
	}






	public JSONArray getDatesToInJSON(List<Calendar> datesTo) throws JSONException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		JSONArray datesToArray = new JSONArray();
		if (datesTo.size() > 0)
			for (Calendar time : datesTo) {
				if (datesToArray.length() >= 5)
					break;
				boolean equal = false;
				for (int i = 0; i < datesToArray.length(); i++)
					if (datesToArray.getString(i).equalsIgnoreCase(sdf.format(time.getTime())))
						equal = true;
				if (!equal)
					datesToArray.put(sdf.format(time.getTime()));
			}
		else
			datesToArray.put("");

		return datesToArray;
	}






	public JSONArray getTimesFromInJSON(List<Calendar> datesFrom) throws JSONException {

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		JSONArray timesFromArray = new JSONArray();
		if (datesFrom.size() > 0)
			for (Calendar time : datesFrom) {
				if (timesFromArray.length() >= 5)
					break;
				boolean equal = false;
				for (int i = 0; i < timesFromArray.length(); i++)
					if (timesFromArray.getString(i).equalsIgnoreCase(sdf.format(time.getTime())))
						equal = true;
				if (!equal)
					timesFromArray.put(sdf.format(time.getTime()));
			}

		else
			timesFromArray.put("");

		return timesFromArray;
	}






	public JSONArray getTimesToInJSON(List<Calendar> datesTo) throws JSONException {

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		JSONArray timesToArray = new JSONArray();
		if (datesTo.size() > 0)
			for (Calendar time : datesTo) {
				if (timesToArray.length() >= 5)
					break;
				boolean equal = false;
				for (int i = 0; i < timesToArray.length(); i++)
					if (timesToArray.getString(i).equalsIgnoreCase(sdf.format(time.getTime())))
						equal = true;
				if (!equal)
					timesToArray.put(sdf.format(time.getTime()));
			}
		else
			timesToArray.put("");

		return timesToArray;
	}



}
