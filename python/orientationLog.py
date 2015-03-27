import numpy as np
import matplotlib.pyplot as plt

def getStringInRow(row, stringToFind):
	print row
	print row[0] == stringToFind
	return row[0] == stringToFind

logdata = np.genfromtxt('log_orientation.txt', usecols=(5,6), dtype=None)
# logdata = numpy.loadtxt('logdata.txt', usecols=(5,6))

bool_arr = np.array([getStringInRow(row, "roll:") for row in logdata])
ax = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "pitch:") for row in logdata])
ay = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "yaw:") for row in logdata])
az = np.array([x[1] for x in logdata[bool_arr]])


accelData = [["orientation", ax[i], ay[i], az[i]] for i in range(ax.size)]
# concatenated = ax

np.savetxt("logOrientation.txt", np.array(accelData), fmt='%s')
