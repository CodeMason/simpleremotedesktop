<a href='http://code.google.com/p/simpleremotedesktop/wiki/Experimental_Environment'>Previous: Experimental Environment</a>   <a href='http://code.google.com/p/simpleremotedesktop/wiki/Screenshots'>Next: Screenshots</a>

<h2>Result of the experiments with SRD:</h2>
  * How long SRD takes to move mouse
  * How long SRD takes to type characters
  * How long SRD takes to transfer file using Drag and Drop
  * How much SRD uses network resource

<h2>The results of actions on SRD</h2>
Actions include moving mouse,typeing characters and file transfers using drag and drop.

> <table border='1'>
<blockquote><tr>
<blockquote><th><b> Each Action </b></th><th><b> Time (sec) </b></th>
</blockquote></tr>
<tr>
<blockquote><td> Moving mouse </td><td> 0.12 </td>
</blockquote></tr>
<tr>
<blockquote><td> Typing characters </td><td> 0 (infinitely close to zero)</td>
</blockquote></tr>
<tr>
<blockquote><td> File transfer (size:61.4 KB) </td><td>7.45</td>
</blockquote></tr>
<tr>
<blockquote><td> File transfer (size:4.94 MB) </td><td>331.086</td>
</blockquote></tr>
</table></blockquote>

<h2> The results of network resource on SRD</h2>
Network resource includes downloads and uploads when the client and server in the initial state and when they are in actions.

**The server and the client are in the initial state.**

The first through fourth columns are on client and the fifth through eighth columns are on server and the first row is downloads and the second row is uploads.

> <table border='1'>
<blockquote><tr>
<blockquote><th>Traffic/sec</th><th>Peek/sec</th><th>Average/sec</th><th>Total</th><th> Traffic/sec</th><th>Peek/sec</th><th>Average/sec</th><th>Total</th>
</blockquote></tr>
<tr>
<blockquote><td>0B</td><td>4KB</td><td>3KB</td><td>24KB</td><td>0B</td><td>4KB</td><td>16KB</td><td>4MB</td>
</blockquote></tr>
<tr>
<blockquote><td>0B</td><td>298B</td><td>176B</td><td>352B</td><td>0B</td><td>391B</td><td>378KB</td><td>107MB</td>
</blockquote></tr>
</blockquote><blockquote></table></blockquote>

**The server and the client are just connected each other.**

The first through fourth columns are on client and the fifth through eighth columns are on server and the first row is downloads and the second row is uploads.

> <table border='1'>
<blockquote><tr>
<blockquote><th>Traffic/sec</th><th>Peek/sec</th><th>Average/sec</th><th>Total</th><th> Traffic/sec</th><th>Peek/sec</th><th>Average/sec</th><th>Total</th>
</blockquote></tr>
<tr>
<td>437KB</td><td>583KB</td><td>349KB</td><td>46MB</td><td>128B</td><td>29KB</td><td>16KB</td><td>5MB</td>
</tr>
<tr>
<blockquote><td>100B</td><td>40KB</td><td>13KB</td><td>770KB</td><td>433KB</td><td>536KB</td><td>384KB</td><td>160MB</td>
</blockquote></tr>
</blockquote><blockquote></table></blockquote>

**The server and the client talk each other typing characters,clicking mouse,moving mouse wheel.**

The first through fourth columns are on client and the fifth through eighth columns are on server and the first row is downloads and the second row is uploads.

> <table border='1'>
<blockquote><tr>
<blockquote><th>Traffic/sec</th><th>Peek/sec</th><th>Average/sec</th><th>Total</th><th> Traffic/sec</th><th>Peek/sec</th><th>Average/sec</th><th>Total</th>
</blockquote></blockquote><blockquote></tr>
<tr>
<blockquote><td>376KB</td><td>583KB</td><td>349KB</td><td>114KB</td><td>10KB</td><td>29KB</td><td>14KB</td><td>5MB</td>
</blockquote></tr>
<tr>
<blockquote><td>10KB</td><td>40KB</td><td>10LB</td><td>1MB</td><td>368KB</td>

<Td>

536KB<br>
<br>
Unknown end tag for </td><br>
<br>
<td>378KB</td><td>204MB</td>
</blockquote>

Unknown end tag for </tr>

<br>
</blockquote><blockquote>

Unknown end tag for </table>

</blockquote>

<a href='http://code.google.com/p/simpleremotedesktop/wiki/Experimental_Environment'>Previous: Experimental Environment</a>   <a href='http://code.google.com/p/simpleremotedesktop/wiki/Screenshots'>Next: Screenshots</a>