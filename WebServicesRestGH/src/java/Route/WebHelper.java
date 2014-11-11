/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Route;

import com.graphhopper.util.PointList;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 *
 * @author Amal Elgammal: A copy of WebHelper inside Web package
 */
public class WebHelper {
     public static String encodeURL( String str )
    {
        try
        {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception _ignore)
        {
            return str;
        }
    }

    public static PointList decodePolyline( String encoded, int initCap, boolean is3D )
    {
        PointList poly = new PointList(initCap, is3D);
        int index = 0;
        int len = encoded.length();
        int lat = 0, lng = 0, ele = 0;
        while (index < len)
        {
            // latitude
            int b, shift = 0, result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int deltaLatitude = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += deltaLatitude;

            // longitute
            shift = 0;
            result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int deltaLongitude = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += deltaLongitude;

            if (is3D)
            {
                // elevation
                shift = 0;
                result = 0;
                do
                {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int deltaElevation = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                ele += deltaElevation;
                poly.add((double) lat / 1e5, (double) lng / 1e5, (double) ele / 100);
            } else
                poly.add((double) lat / 1e5, (double) lng / 1e5);
        }
        return poly;
    }

    // https://developers.google.com/maps/documentation/utilities/polylinealgorithm?hl=de
    public static String encodePolyline( PointList poly )
    {
        if (poly.isEmpty())
            return "";
        
        return encodePolyline(poly, poly.is3D());
    }

    public static String encodePolyline( PointList poly, boolean includeElevation )
    {
        StringBuilder sb = new StringBuilder();
        int size = poly.getSize();
        int prevLat = 0;
        int prevLon = 0;
        int prevEle = 0;
        for (int i = 0; i < size; i++)
        {
            int num = (int) Math.floor(poly.getLatitude(i) * 1e5);
            encodeNumber(sb, num - prevLat);
            prevLat = num;
            num = (int) Math.floor(poly.getLongitude(i) * 1e5);
            encodeNumber(sb, num - prevLon);
            prevLon = num;
            if (includeElevation)
            {
                num = (int) Math.floor(poly.getElevation(i) * 100);
                encodeNumber(sb, num - prevEle);
                prevEle = num;
            }
        }
        return sb.toString();
    }

    private static void encodeNumber( StringBuilder sb, int num )
    {
        num = num << 1;
        if (num < 0)
        {
            num = ~num;
        }
        while (num >= 0x20)
        {
            int nextValue = (0x20 | (num & 0x1f)) + 63;
            sb.append((char) (nextValue));
            num >>= 5;
        }
        num += 63;
        sb.append((char) (num));
    }

    public static String readString( InputStream inputStream ) throws IOException
    {
        String encoding = "UTF-8";
        InputStream in = new BufferedInputStream(inputStream, 4096);
        try
        {
            byte[] buffer = new byte[4096];
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int numRead;
            while ((numRead = in.read(buffer)) != -1)
            {
                output.write(buffer, 0, numRead);
            }
            return output.toString(encoding);
        } finally
        {
            in.close();
        }
    }
    
}
