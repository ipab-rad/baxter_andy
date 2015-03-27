import numpy as np
import matplotlib.pyplot as plt

def getStringInRow(row, stringToFind):
	print row
	print row[0] == stringToFind
	return row[0] == stringToFind

logdata = np.genfromtxt('log_moving_avg.txt', usecols=(5,6), dtype=None)
# logdata = numpy.loadtxt('logdata.txt', usecols=(5,6))

bool_arr = np.array([getStringInRow(row, "accelX:") for row in logdata])
ax = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "accelY:") for row in logdata])
ay = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "accelZ:") for row in logdata])
az = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "velocityX:") for row in logdata])
vx = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "velocityY:") for row in logdata])
vy = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "velocityZ:") for row in logdata])
vz = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "positionX:") for row in logdata])
px = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "positionY:") for row in logdata])
py = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "positionZ:") for row in logdata])
pz = np.array([x[1] for x in logdata[bool_arr]])

bool_arr = np.array([getStringInRow(row, "timestamp:") for row in logdata])
startTime = logdata[bool_arr][0][1]
endTime = logdata[bool_arr][-1][1] - startTime
timestamp = np.array([x[1] - startTime for x in logdata[bool_arr]])


accelData = [["accel", ax[i], ay[i], az[i]] for i in range(ax.size)]
velocityData = [["velocity", vx[i]/1000, vy[i]/1000, vz[i]/1000] for i in range(vx.size)]
positionData = [["position", px[i], py[i], pz[i]] for i in range(px.size)]
# concatenated = ax

np.savetxt("logDataAccel.txt", np.array(accelData), fmt='%s')
np.savetxt("logDataVelocity.txt", np.array(velocityData), fmt='%s')
np.savetxt("logDataPosition.txt", np.array(positionData), fmt='%s')

plt.figure(1)

plt.subplot(331)
plt.axis([0,endTime,-3,3])
plt.plot(timestamp, ax)

plt.subplot(332)
plt.axis([0,endTime,-3,3])
plt.plot(timestamp, ay)

plt.subplot(333)
plt.axis([0,endTime,-3,3])
plt.plot(timestamp, az)


plt.subplot(334)
plt.axis([0,endTime,-1,1])
plt.plot(timestamp, vx)

plt.subplot(335)
plt.axis([0,endTime,-1,1])
plt.plot(timestamp, vy)

plt.subplot(336)
plt.axis([0,endTime,-1,1])
plt.plot(timestamp, vz)


plt.subplot(337)
plt.axis([0,endTime,-1.5,1.5])
plt.plot(timestamp, px)

plt.subplot(338)
plt.axis([0,endTime,-1.5,1.5])
plt.plot(timestamp, py)

plt.subplot(339)
plt.axis([0,endTime,-1.5,1.5])
plt.plot(timestamp, pz)


plt.show()
