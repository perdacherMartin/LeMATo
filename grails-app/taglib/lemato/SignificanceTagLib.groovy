package lemato

import lemato.nopersistence.SignificanceBucket

class SignificanceTagLib {
    static defaultEncodeAs = [taglib:'text']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    //
    
    def buildScatterChart = { attrs, body ->

    	final List<SignificanceBucket> significanceBucketList = attrs.get('significanceBucketList')
    	final String textUnitName = attrs.get('textUnitName')
    	final String divTagName = attrs.get('divTagName')

    	out << """
    	<script type=\"text/javascript\">
          google.setOnLoadCallback(drawCharts);
          function drawCharts() {

            var data = [];            
            
            data = google.visualization.arrayToDataTable([
                ['Frequency','${textUnitName} frequency, Score'], 
       	"""
       		significanceBucketList.eachWithIndex { bucket, j -> 
       			if ( j != 0 ){ out << ", " }
       			out << "[ { v: " + bucket.docCount + ", f:'${bucket.key}, ${bucket.docCount}' }, " + bucket.score + "]"
       		}

            out << "]);"

			out << """
            var options = {
              title: '${textUnitName} frequency vs. Score comparison',
              hAxis: {title: '${textUnitName} frequency', minValue: 0, maxValue: 15},
              vAxis: {title: 'Score', minValue: 0, maxValue: 15},
              legend: 'none',
              explorer: {
                actions: ['dragToZoom', 'rightClickToReset']    
              }
            };

            var chart = new google.visualization.ScatterChart(document.getElementById('${divTagName}'));

            chart.draw(data, options);            
          }
        </script>
        """
    }
}
