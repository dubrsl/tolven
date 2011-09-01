CREATE INDEX touch_index1
  ON public.touch
  USING btree
  (account_id,updateplaceholder_id);

CREATE INDEX md_index1
  ON app.menu_data
  USING btree
  (menustructure_id);

CREATE INDEX md_index2
  ON app.menu_data
  USING btree
  (menustructure_id, parent01_id);

CREATE INDEX md_index3
  ON app.menu_data
  USING btree
  (trimheader_id);
  
CREATE INDEX md_index4
  ON app.menu_data
  USING btree
  (account_id, document_id);

CREATE INDEX md_index5
  ON app.menu_data
  USING btree
  (account_id, menu_path);

CREATE INDEX md_index6 
  ON app.menu_data 
  USING btree
  (account_id, reference_id);


CREATE UNIQUE INDEX mdv_index1
  ON app.menu_data_version
  USING btree
  (account_id, element);

CREATE INDEX mdw_index1
  ON app.menu_data_word
  USING btree
  (menudata_id);

CREATE INDEX mdw_index2
  ON app.menu_data_word
  USING btree
  (menustructure_id, word, menudata_id);
    
CREATE UNIQUE INDEX ph_index2
  ON app.placeholder_id
  USING btree
  (account_id, menustrucuture_id, id_root, id_extension);

CREATE INDEX ms_index1
  ON app.menu_structure
  USING btree
  (account_id, path_name);
  
CREATE INDEX mcol_index1
  ON app.ms_column
  USING btree
  (menustructure_id, account_id);

CREATE INDEX ums_index1
  ON app.user_menu_structure
  USING btree
  (accountuser_id, underlyingms_id);

CREATE INDEX au_index1
  ON core.account_user
  USING btree
  (user_id);

CREATE INDEX user_index1
  ON core.tolven_user
  USING btree
  (uid, status, id);

CREATE INDEX doc_index1
  ON doc.document
  USING btree
  (account_id, xml_name);

CREATE INDEX th_index1
  ON app.trim_header
  USING btree
  (name,status);

CREATE INDEX th_index2
  ON app.trim_header
  USING btree
  (status, id);
  
CLUSTER md_index1 ON app.menu_data;
