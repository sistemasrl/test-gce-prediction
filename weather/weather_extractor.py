from datetime import date
import calendar
import urllib2
import json
import os
import sys, getopt
import logging
from logging import config

URI = "http://api.worldweatheronline.com/premium/v1/past-weather.ashx?" \
      "q={lat},{lon}&format=json&date={from}&enddate={to}&tp=1&key=ab5b39c44a127d3e16312f8aa40b4"

# INIT LOG <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
# LOGGING_CONFIG_FILE_PATH = os.path.realpath("logging.conf")
#logging.config.fileConfig(LOGGING_CONFIG_FILE_PATH)
logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s %(levelname)s %(message)s')
log = logging.getLogger()


def run(argv):
    yearFrom = None
    yearTo = None
    lat = "0.0"
    lon = "0,0"
    downloadData = False
    buildCsv = False


    # Parse command line parameters
    try:
        opts, args = getopt.getopt(argv, "hdb", [
            "help"
            , "yearFrom="
            , "yearTo="
            , "lat="
            , "lon="
            , "download"
            , "build"
        ])
    except getopt.GetoptError:
        log.exception("ERROR parsing command line arguments")
        sys.exit(2)

    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print(__help())
            sys.exit()
        elif opt == "--yearFrom":
            yearFrom = arg
        elif opt == "--yearTo":
            yearTo = arg
        elif opt == "--lat":
            lat = arg
        elif opt == "--lon":
            lon = arg
        elif opt in ("-d", "--download"):
            downloadData = True
        elif opt in ("-b", "--build"):
            buildCsv = True

    if downloadData:
        if yearFrom is None and yearTo is None:
            print(__help())
            sys.exit(2)
        elif yearFrom is None:
            yearFrom = yearTo
        elif yearTo is None:
            yearTo = yearFrom



    log.info(os.linesep.join([
        os.linesep
        , "----------- Input Data -----------: "
        , "Running with: "
        , "--yearFrom:    " + (yearFrom if yearFrom is not None else "")
        , "--yearTo:      " + (yearTo if yearTo is not None else "")
        , "--lat:         " + (lat if lat is not None else "")
        , "--lon:         " + (lon if lon is not None else "")
        , "--download:    " + (str(downloadData))
        , "--build:       " + (str(buildCsv))
    ]))


    # Per ogni mese faccio una richiesta e salvo gli xml (cosi li metto da parte)
    # Poi li parso in un unico csv
    if downloadData:

        yearFrom = int(yearFrom)
        yearTo = int(yearTo)

        for year in range(yearFrom, yearTo + 1):
            for x in range(1, 13):
                dfrom = date(year, x, 1)
                dto = date(year, x, calendar.monthrange(year, x)[1])
                print("Downloading %s - %s" % (dfrom, dto))
                weatherData = urllib2.urlopen(URI.replace("{from}", str(dfrom))
                                              .replace("{to}", str(dto))
                                              .replace("{lat}",lat)
                                              .replace("{lon}",lon)
                )
                fileName = "weather_%s_%s.json" % (dfrom, dto)
                with open(fileName, 'wb') as out:
                    out.write(weatherData.read())

    # Build weatherinfo.csv as
    # date[dd/MM/yyyy HH:mm:ss], temp[C], wind [km/h], weatherCode,precip[mm],visibility[km]
    # For weatehr code http://www.worldweatheronline.com/feed/wwoConditionCodes.txt
    if buildCsv:
        out = open("weather.csv",'w+')
        out.write(",".join(['date','cloudcover','humidity','precipMM','pressure','visibility','windspeedKmph'])+"\n")

        for jfile in os.listdir('./'):
            if jfile.endswith(".json"):
                with open(jfile, 'r') as data_file:
                    data = json.load(data_file)
                    for weather in (data['data']['weather']):
                        for hourWeather in weather['hourly']:
                            csvRow = []
                            myDate = weather['date'] + " " + __getHHmm(hourWeather['time'])
                            csvRow.append(myDate)
                            csvRow.append(hourWeather['cloudcover'])
                            csvRow.append(hourWeather['humidity'])
                            csvRow.append(hourWeather['precipMM'])
                            csvRow.append(hourWeather['pressure'])
                            csvRow.append(hourWeather['visibility'])
                            csvRow.append(hourWeather['windspeedKmph'])
                            out.write(",".join(csvRow)+"\n")



def __help():
    """
    :rtype: str
    """
    return os.linesep.join([
        os.linesep
        , "-h, --help:               usage"
        , "--yearFrom:               First year to download, at least one between \"yearFrom\" and \"yearTo\" if \"downloadData\" is True"
        , "--yearTo:                 Last year to downlod, at least one between \"yearFrom\" and \"yearTo\" if \"downloadData\" is True"
        , "--lat:                    Latitude in decimal degree"
        , "--lon:                    Longitude in decimal degree"
        , "-d, --download:           download weather data into json files"
        , "-b, --build:              build csv from json"
        , os.linesep
    ])


def __getHHmm(s):
    """
    From a string HHmm return a string of type: HH:mm
    :param str s: HHmm formatted string
    :return:
    """
    if len(s)>4:
        raise Exception("Input string for HH:mm must have 4 characters")
    h = "00"
    m = "00"
    # get hour
    if len(s) == 1:
        m = "0" + s
    elif len(s) == 2:
        m = s
    elif len(s) == 3:
        h = "0" + s[:1]
        m = s[1:]
    elif len(s) == 4:
        h = s[:2]
        m = s[2:]

    return h + ":" + m




run(sys.argv[1:])


