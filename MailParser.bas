Type=StaticCode
Version=7.01
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
'Code module
Sub Process_Globals
	Type Message (FromField As String, ToField As String, CCField As String, BCCField As String, _
		Subject As String, Body As String, ContentType As String, Attachments As List)
	Dim index As Int
	Dim boundary As String
	Dim multipart As Boolean
	Dim dir As String
End Sub
'Parses a raw mail message and returns a Message object
'Mail - The mail raw text
'AttachmentsDir - Attachments will be saved in this folder
Sub ParseMail (Mail As String, AttachmentsDir As String) As Message
	index = 0
	multipart = False
	boundary = ""
	Dim msg As Message
	msg.Initialize
	msg.Attachments.Initialize
	ParseHeaders(Mail, msg)
	If multipart = False Then
		ParseSimpleBody(Mail, msg)
	Else
		dir = AttachmentsDir
		ParseMultipartBody(Mail, msg)
	End If
	Return msg
End Sub
Sub ParseMultipartBody (Mail As String, Msg As Message)
	'find first boundary
	index = Mail.IndexOf2("--" & boundary, index)
	ReadNextLine(Mail)
	Dim headers As StringBuilder
	headers.Initialize
	Do While index < Mail.Length
		Dim line As String
		line = ReadNextLine(Mail)
		If line.Length > 0 Then
			headers.Append(line).Append(" ")
		Else If index < Mail.Length Then
			Dim nextPart As Int
			nextPart = Mail.IndexOf2("--" & boundary, index)
			If nextPart-4 > index Then
				HandlePart(headers.ToString, Mail.SubString2(index, nextPart-4), Msg)
			End If
			If nextPart = -1 Then Return
			index = nextPart
			ReadNextLine(Mail)
			headers.Initialize
		End If
	Loop
End Sub
Sub HandlePart(Headers As String, Body As String, Msg As Message)
	If Regex.Matcher2("Content-Transfer-Encoding:\s*base64", _
		Regex.CASE_INSENSITIVE, Headers).Find Then
		'we are dealing with an attachment
		Dim filename As String
		Dim m As Matcher
		m = Regex.Matcher2("filename=\s*q([^q]+)q".Replace("q", QUOTE), Regex.CASE_INSENSITIVE, Headers)
		If m.Find Then filename = m.Group(1) Else filename = "attachment" & (Msg.Attachments.Size + 1)
		Dim su As StringUtils
		Dim out As OutputStream
		out = File.OpenOutput(dir, filename, False)
		Dim data() As Byte
		data = su.DecodeBase64(Body)
		Log("file saved: "  & filename & " (" & data.Length & " bytes)")
		out.WriteBytes(data, 0, data.Length)
		out.Close
		msg.Attachments.Add(filename)
	Else If Regex.Matcher2("Content-Type:\s*text/", _
		Regex.CASE_INSENSITIVE, Headers).Find Then
		msg.Body = Body
	End If
End Sub

Sub ParseSimpleBody (Mail As String, Msg As Message)
	msg.Body = Mail.SubString(index)
End Sub

Sub ParseHeaders (Mail As String, Msg As Message)
	Dim line As String
	line = ReadNextLine(Mail)
	Do While line.Length > 0
		Dim parts() As String
		parts = Regex.Split(":", line)
		If parts.Length >= 2 Then
			Dim first As String
			first = parts(0).ToLowerCase
			Select first
				Case "from"
					msg.FromField = parts(1)
				Case "to"
					msg.ToField = parts(1)
				Case "cc"
					msg.CCField = parts(1)
				Case "bcc"
					msg.BCCField = parts(1)
				Case "subject"
					msg.Subject = parts(1)
				Case "content-type"
					Dim second As String
					second =  parts(1).ToLowerCase
					msg.ContentType = parts(1)
					If second.Contains("multipart/") Then
						multipart = True
						If FindBoundary(line) = False Then
							line = ReadNextLine(Mail)
							FindBoundary(line)
						End If
					End If
			End Select	
		End If
		line = ReadNextLine(Mail)
	Loop
End Sub

Sub FindBoundary(line As String) As Boolean
	Dim m As Matcher
	m = Regex.Matcher2("boundary=\q([^q]+)\q".Replace("q", QUOTE), Regex.CASE_INSENSITIVE, line)
	If m.Find Then
		boundary = m.Group(1)
		Return True
	Else
		Return False
	End If
End Sub

Sub ReadNextLine (Mail As String)
	Dim sb As StringBuilder
	sb.Initialize
	Dim c As Char
	Do While index < Mail.Length
		c = Mail.CharAt(index)
		index = index + 1
		If c = Chr(13) OR c = Chr(10) Then
			If c = Chr(13) AND index < Mail.Length AND Mail.CharAt(index) = Chr(10) Then
				index = index + 1
			End If
			Exit 'break the loop
		End If
		sb.Append(c)
	Loop
	Return sb.ToString
End Sub