<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<searchForm xmlns="http://www.digitech.com/dossiers/search" xmlns:ns2="http://www.digitech.com/dossiers/common" customerName="TRADITION" version="4.0.9 RC1">
    <organization id="0" name="DEFAULT"/>
    <organization id="3" name="RISK">
        <globalSearch>
            <ns2:dossierrequest name="globalSearch">
                <ns2:contentType>RK_DOCUMENT</ns2:contentType>
                <ns2:airsvalue>REFAIRS=&quot;#VALUE#&quot;</ns2:airsvalue>
            </ns2:dossierrequest>
        </globalSearch>
        <contentType name="RK_DOCUMENT">
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Date inserted</label>
                <airsfield>
                    <codeField>D_CREAT</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <label>Document identification</label>
                <airsfield>
                    <codeField>REFAIRS</codeField>
                </airsfield>
                <initial-value></initial-value>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <label>Document Name</label>
                <airsfield>
                    <codeField>DOCNAME</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Author</label>
                <airsfield>
                    <codeField>AUTHOR</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Date created</label>
                <airsfield>
                    <codeField>D_CREAT_DOC</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Last modified</label>
                <airsfield>
                    <codeField>D_MODIF</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Modified by</label>
                <airsfield>
                    <codeField>U_MODIFIED</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Nature of the document</label>
                <airsfield>
                    <codeField>RK_AL_NATURE</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Status of the document</label>
                <airsfield>
                    <codeField>RK_AL_STATUS</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <initial-value></initial-value>
                <required>false</required>
		<readOnly>true</readOnly>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Type of risks</label>
                <airsfield>
                    <codeField>RK_AL_RISKS</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Company</label>
                <airsfield>
                    <codeField>RK_AL_COMPANY</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Domain</label>
                <airsfield>
                    <codeField>RK_AL_DOMAIN</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Description</label>
                <airsfield>
                    <codeField>DESCRIPTION</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
        </contentType>
    </organization>
    <organization id="2" name="TRADOC">
        <globalSearch>
            <ns2:dossierrequest name="globalSearch">
                <ns2:contentType>TD_DOCUMENT</ns2:contentType>
                <ns2:airsvalue>REFAIRS=&quot;#VALUE#&quot;</ns2:airsvalue>
            </ns2:dossierrequest>
        </globalSearch>
        <contentType name="TD_DOCUMENT">
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Document type</label>
                <airsfield>
                    <codeField>TD_GENRE</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Date inserted</label>
                <airsfield>
                    <codeField>D_CREAT</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <label>Document identification</label>
                <airsfield>
                    <codeField>REFAIRS</codeField>
                </airsfield>
                <initial-value></initial-value>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Last modified</label>
                <airsfield>
                    <codeField>D_MODIF</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Author</label>
                <airsfield>
                    <codeField>AUTHOR</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Function</label>
                <airsfield>
                    <codeField>TD_FUNCTION</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Domain</label>
                <airsfield>
                    <codeField>TD_DOMAIN</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Status of the document</label>
                <airsfield>
                    <codeField>TD_STATUS</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Description</label>
                <airsfield>
                    <codeField>DESCRIPTION</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Document name</label>
                <airsfield>
                    <codeField>DOCNAME</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
        </contentType>
        <contentType name="TD_DOSSIER">
            <input>
                <operator>=</operator>
                <label>Function</label>
                <airsfield>
                    <codeField>TD_FUNCTION</codeField>
                </airsfield>
                <initial-value></initial-value>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Date inserted</label>
                <airsfield>
                    <codeField>D_CREAT</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
            <input>
                <operator>=</operator>
                <operator>&lt;</operator>
                <operator>&gt;</operator>
                <operator>{}</operator>
                <label>Last modified</label>
                <airsfield>
                    <codeField>D_MODIF</codeField>
                </airsfield>
                <inputHelper autoTab="false"/>
                <required>false</required>
                <allowJoker>true</allowJoker>
            </input>
        </contentType>
    </organization>
</searchForm>
