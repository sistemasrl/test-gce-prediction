from datetime import date
import calendar
import urllib2
import json
import os

URI = "http://api.worldweatheronline.com/premium/v1/past-weather.ashx?" \
      "q=49.3603&0.8362&format=json&date={from}&enddate={to}&tp=1&key=ab5b39c44a127d3e16312f8aa40b4"

ACTION_DOWNLOAD_DATA = True
ACTION_BUILD_CSV_DATA = False

YEAR = 2012


# Per ogni mese faccio una richiesta e salvo gli xml (cosi li metto da parte)
# Poi li parso in un unico csv
if ACTION_DOWNLOAD_DATA:
    for x in range(1, 13):
        dfrom = date(YEAR, x, 1)
        dto = date(YEAR, x, calendar.monthrange(YEAR, x)[1])
        print "Downloading %s - %s" % (dfrom, dto)

        weatherData = urllib2.urlopen(URI.replace("{from}", str(dfrom)).replace("{to}", str(dto)))
        fileName = "weather_%s_%s.json" % (dfrom, dto)
        with open(fileName, 'wb') as out:
            out.write(weatherData.read())

# Build weatherinfo.csv as
# date[dd/MM/yyyy HH:mm:ss], temp[C], wind [km/h], weatherCode,precip[mm],visibility[km]
# For weatehr code http://www.worldweatheronline.com/feed/wwoConditionCodes.txt
if ACTION_BUILD_CSV_DATA:
    out = open("weather.csv")

    for jfile in os.listdir('./'):
        if file.endswith(".json"):
            with open(jfile, 'r') as data_file:
                data = json.load(data_file)
                for weather in (data['data']['weather']):
                    out.write("")







