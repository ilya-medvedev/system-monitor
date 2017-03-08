const chart = {
    type: 'spline',
    animation: Highcharts.svg
};

const xAxis = {
    type: 'datetime',
    maxZoom: 60000
};

const titleWithoutText = {
    text: false
};

const yAxisWithoutTitle = {
    title: titleWithoutText
};

const sharedPercentTooltip = {
    valueSuffix: '%',
    shared: true
};

const plotWithoutMarkerOptions = {
    spline: {
        marker: {
            enabled: false
        }
    }
};

const disabledExporting = {
    enabled: false
};

$('#cpu').highcharts({
    chart: chart,
    title: {
        text: 'CPU'
    },
    xAxis: xAxis,
    yAxis: yAxisWithoutTitle,
    tooltip: sharedPercentTooltip,
    plotOptions: plotWithoutMarkerOptions,
    exporting: disabledExporting,
    series: [
        {
            id: 'cpu',
            name: 'cpu'
        }
    ]
});

$('#mem').highcharts({
    chart: chart,
    title: {
        text: 'Memory'
    },
    xAxis: xAxis,
    yAxis: yAxisWithoutTitle,
    tooltip: sharedPercentTooltip,
    plotOptions: plotWithoutMarkerOptions,
    exporting: disabledExporting,
    series: [
        {
            id: 'mem',
            name: 'mem'
        }
    ]
});

function bytesPerSecondLabel(bytes) {
    const suffix = ['B/s', 'kB/s', 'MB/s', 'GB/s'];

    if (bytes === 0) {
        return '0 B/s';
    }

    const e = Math.floor(Math.log(bytes) / Math.log(1024));
    const value = Math.round((bytes / Math.pow(1024, e)) * 100) / 100;

    return value + ' ' + suffix[e];
}

const bytesYAxis = {
    title: titleWithoutText,
    labels: {
        formatter: function() {
            return bytesPerSecondLabel(this.value);
        }
    }
};

function pointFormatter(point) {
    const series = point.series;

    const mark = '<font color="' + series.color + '">●</font>';
    const name = ' ' + series.name + ': ';
    const value = '<b>' + bytesPerSecondLabel(point.y) + '</b>';

    return mark + name + value + '<br/>';
}

const bytesTooltip = {
    pointFormatter: function() {
        return pointFormatter(this);
    },
    useHTML: true,
    shared: true
};

$('#disk').highcharts({
    chart: chart,
    title: {
        text: 'Disk'
    },
    xAxis: xAxis,
    yAxis: bytesYAxis,
    tooltip: bytesTooltip,
    plotOptions: plotWithoutMarkerOptions,
    exporting: disabledExporting,
    series: [
        {
            id: 'read',
            name: 'Read'
        },
        {
            id: 'write',
            name: 'Write'
        }
    ]
});

$('#net').highcharts({
    chart: chart,
    title: {
        text: 'Network'
    },
    xAxis: xAxis,
    yAxis: bytesYAxis,
    tooltip: bytesTooltip,
    plotOptions: plotWithoutMarkerOptions,
    exporting: disabledExporting,
    series: [
        {
            id: 'down',
            name: 'Download'
        },
        {
            id: 'up',
            name: 'Upload'
        }
    ]
});

function onMessage(message) {
    const sensorMessage = proto.SensorMessage.deserializeBinary(message.data);
    const value = sensorMessage.getValueList();

    $.each(value, function(index, sensorInfo) {
        const name = sensorInfo.getName();
        const div = '#' + name;
        const chart = $(div).highcharts();
        const value = sensorInfo.getValueList();

        $.each(value, function(index, sensorValue) {
            const name = sensorValue.getName();
            const series = chart.get(name);
            const time = sensorMessage.getTime();
            const value = sensorValue.getValue();
            const point = [time, value];

            if (series === null) {
                chart.addSeries({
                    id: name,
                    name: name,
                    data: [point],
                    visible: false
                }, false);
            } else {
                const shift = series.xData.length > 20;

                series.addPoint(point, false, shift);
            }
        });

        chart.redraw();
    });
}

$(function() {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    goog.require('proto.SensorMessage');

    const protocol = location.protocol.replace('http', 'ws');
    const url = protocol + '//' + location.host + '/ws';
    const ws = new WebSocket(url);

    ws.binaryType = "arraybuffer";
    ws.onmessage = onMessage;
});
