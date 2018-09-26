using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using Microsoft.ProjectOxford.Face;
using Microsoft.ProjectOxford.Face.Contract;
using System.IO;
using System.Net.Http.Headers;
namespace router.Controllers
{
    public class ValuesController : ApiController
    {
        string kq = "Nguyễn Thanh Huy";
        FaceServiceClient faceServiceClient = new FaceServiceClient("ec076dfe8cf14f38b4b1860d07e65e32", "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/");

        // GET api/values
        //public IEnumerable<string> Get()
        //{
        //    return new string[] { "value1", "value2" };
        //}

        public async Task <string> Get()
        {
            var result = await RecognitionFaceImgPath("kpop", @"D:\test\gd.jpg");
            // string i = Test();
            return result;
        }


        // GET api/values/5
        public string Get(int id)
        {
            return "value";
        }

        [Route("customers={customerId}")]
        public string Get(string customerId)
        {
            return customerId;
        }
        [HttpPost]
        [Route("data={data}")]
        public string postdata(string data)
        {
            return data;
        }





        [HttpPost, Route("api/upload")]
        public HttpResponseMessage PostImage()

        {
            Console.WriteLine("upload");
            HttpResponseMessage result = null;

            var httpRequest = HttpContext.Current.Request;

            Console.WriteLine("upload :" + httpRequest.Files.Count);

            if (httpRequest.Files.Count > 0)

            {

                var docfiles = new List<string>();

                foreach (string file in httpRequest.Files)

                {

                    var postedFile = httpRequest.Files[file];

                    var filePath = HttpContext.Current.Server.MapPath("~/" + postedFile.FileName);

                    postedFile.SaveAs(filePath);



                    docfiles.Add(filePath);

                }

                result = Request.CreateResponse(HttpStatusCode.Created, docfiles);

            }

            else

            {

                result = Request.CreateResponse(HttpStatusCode.BadRequest);

            }

            return result;

        }





        //[Route("api/upload")]
        //public async Task<HttpResponseMessage> Post()
        //{
        //    try
        //    {
        //        if (!Request.Content.IsMimeMultipartContent())
        //        {
        //            throw new HttpResponseException(HttpStatusCode.UnsupportedMediaType);
        //        }

        //        //Save To this server location
        //        var uploadPath = HttpContext.Current.Server.MapPath("~/test");
        //        //The reason we not use the default MultipartFormDataStreamProvider is because
        //        //the saved file name is look weird, not believe me? uncomment below and try out, 
        //        //the odd file name is designed for security reason -_-'.
        //        //var streamProvider = new MultipartFormDataStreamProvider(uploadPath);

        //        //Save file via CustomUploadMultipartFormProvider
        //        var multipartFormDataStreamProvider = new UploadFile.Custom.CustomUploadMultipartFormProvider(uploadPath);

        //        // Read the MIME multipart asynchronously 
        //        await Request.Content.ReadAsMultipartAsync(multipartFormDataStreamProvider);

        //        // Show all the key-value pairs.
        //        foreach (var key in multipartFormDataStreamProvider.FormData.AllKeys)
        //        {
        //            foreach (var val in multipartFormDataStreamProvider.FormData.GetValues(key))
        //            {
        //                Console.WriteLine(string.Format("{0}: {1}", key, val));
        //            }
        //        }


        //        //In Case you want to get the files name
        //        //string localFileName = multipartFormDataStreamProvider
        //        //    .FileData.Select(multiPartData => multiPartData.LocalFileName).FirstOrDefault();


        //        return new HttpResponseMessage(HttpStatusCode.OK);


        //    }
        //    catch (Exception e)
        //    {
        //        return new HttpResponseMessage(HttpStatusCode.NotImplemented)
        //        {
        //            Content = new StringContent(e.Message)
        //        };
        //    }


        //}




        [Route("user/PostUserImage")]
        public async Task<HttpResponseMessage> PostUserImage()
        {
            Dictionary<string, object> dict = new Dictionary<string, object>();
            try
            {

                var httpRequest = HttpContext.Current.Request;

                foreach (string file in httpRequest.Files)
                {
                    HttpResponseMessage response = Request.CreateResponse(HttpStatusCode.Created);

                    var postedFile = httpRequest.Files[file];
                    if (postedFile != null && postedFile.ContentLength > 0)
                    {

                        int MaxContentLength = 1024 * 1024 * 1; //Size = 1 MB

                        IList<string> AllowedFileExtensions = new List<string> { ".jpg", ".gif", ".png" };
                        var ext = postedFile.FileName.Substring(postedFile.FileName.LastIndexOf('.'));
                        var extension = ext.ToLower();
                        if (!AllowedFileExtensions.Contains(extension))
                        {

                            var message = string.Format("Please Upload image of type .jpg,.gif,.png.");

                            dict.Add("error", message);
                            return Request.CreateResponse(HttpStatusCode.BadRequest, dict);
                        }
                        else if (postedFile.ContentLength > MaxContentLength)
                        {

                            var message = string.Format("Please Upload a file upto 1 mb.");

                            dict.Add("error", message);
                            return Request.CreateResponse(HttpStatusCode.BadRequest, dict);
                        }
                        else
                        {

                           
                            //  where you want to attach your imageurl

                            //if needed write the code to update the table

                            var filePath = HttpContext.Current.Server.MapPath("~/Userimage/" + postedFile.FileName + extension);
                            //Userimage myfolder name where i want to save my image
                            postedFile.SaveAs(filePath);

                        }
                    }

                    var message1 = string.Format("Image Updated Successfully.");
                    return Request.CreateErrorResponse(HttpStatusCode.Created, message1); ;
                }
                var res = string.Format("Please Upload a image.");
                dict.Add("error", res);
                return Request.CreateResponse(HttpStatusCode.NotFound, dict);
            }
            catch (Exception ex)
            {
                var res = string.Format("some Message");
                dict.Add("error", res);
                return Request.CreateResponse(HttpStatusCode.NotFound, dict);
            }
        }

        // POST api/values
        public void Post([FromBody]string value)
        {
        }

        // PUT api/values/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/values/5
        public void Delete(int id)
        {
        }







        private async void CreatePersonGroup(String personGroupId, string personGroupName)
        {
            try
            {
                await faceServiceClient.CreatePersonGroupAsync(personGroupId, personGroupName);
                Console.WriteLine("Done " + personGroupName);

            }
            catch (Exception ex)
            {
                Console.WriteLine("Error Create Person Group\n" + ex.Message);
            }
        }
        private async void AddPersonToGroup(String personGroupId, string Name, string pathImage)
        {
            try
            {
                await faceServiceClient.GetPersonGroupAsync(personGroupId);
                CreatePersonResult person = await faceServiceClient.CreatePersonAsync(personGroupId, Name);
                DetectFaceAndRegiter(personGroupId, person, pathImage);
                Console.WriteLine("add " + Name);
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error Add Person To Group\n" + ex.Message);
            }
        }

        private async void DetectFaceAndRegiter(string personGroupId, CreatePersonResult person, string pathImage)
        {
            foreach (var imgPath in Directory.GetFiles(pathImage, "*.jpg"))
            {
                using (Stream s = File.OpenRead(imgPath))
                {
                    await faceServiceClient.AddPersonFaceAsync(personGroupId, person.PersonId, s);
                }
            }
        }

        public async void TrainingAI(string personGroupId)
        {
            await faceServiceClient.TrainPersonGroupAsync(personGroupId);
            TrainingStatus trainingStatus = null;
            while (true)
            {
                trainingStatus = await faceServiceClient.GetPersonGroupTrainingStatusAsync(personGroupId);
                if (trainingStatus.Status != Status.Running)
                    break;
                await Task.Delay(1000);

            }
            Console.WriteLine("Training AI complete");

        }

        public async Task<string> RecognitionFace(string personGroupId, Stream s)
        {
            var faces = await faceServiceClient.DetectAsync(s);
            var faceIds = faces.Select(face => face.FaceId).ToArray();
            try
            {
                var results = await faceServiceClient.IdentifyAsync(personGroupId, faceIds);
                foreach (var identifyResult in results)
                {
                    // Console.WriteLine(string.Format("Result of face: {0} ", identifyResult.FaceId));
                    if (identifyResult.Candidates.Length == 0)
                    {
                        // Console.WriteLine("No one indentify");
                        kq = "No one indentify";
                    }
                    else
                    {
                        var candidateId = identifyResult.Candidates[0].PersonId;
                        var person = await faceServiceClient.GetPersonAsync(personGroupId, candidateId);
                        // Console.WriteLine(string.Format("Identified as: {0}", person.Name));
                        kq = string.Format("Identified as: {0}", person.Name);
                    }
                }
            }
            catch (Exception ex)
            {
                // Console.WriteLine("Error Recognition Face" + ex.Message);
                kq = "Error Recognition Face" + ex;
            }

            return kq;
        }

        public async Task<string> RecognitionFaceImgPath(string personGroupId, string imgPath)
        {


            using (Stream s = File.OpenRead(imgPath))
            {
                var faces = await faceServiceClient.DetectAsync(s);
                var faceIds = faces.Select(face => face.FaceId).ToArray();
                try
                {
                    var results = await faceServiceClient.IdentifyAsync(personGroupId, faceIds);
                    foreach (var identifyResult in results)
                    {
                        // Console.WriteLine(string.Format("Result of face: {0} ", identifyResult.FaceId));
                        if (identifyResult.Candidates.Length == 0)
                        {
                            // Console.WriteLine("No one indentify");
                            kq = "No one indentify";
                        }
                        else
                        {
                            var candidateId = identifyResult.Candidates[0].PersonId;
                            var person = await faceServiceClient.GetPersonAsync(personGroupId, candidateId);
                            // Console.WriteLine(string.Format("Identified as: {0}", person.Name));
                            kq = string.Format("Identified as: {0}", person.Name);
                        }
                    }
                }
                catch (Exception ex)
                {
                    // Console.WriteLine("Error Recognition Face" + ex.Message);
                    kq = "Error Recognition Face" + ex;
                }
            }

            return kq;
        }

        public async void deletePersonGroup(string personGroupId)
        {
            try
            {
                await faceServiceClient.DeletePersonGroupAsync(personGroupId);
                Console.WriteLine("Deleted personGroup ");
            }
            catch (Exception ex)
            {
                Console.WriteLine("Delete error" + ex.Message);
            }
        }









    }
}




namespace UploadFile.Custom
{
    public class CustomUploadMultipartFormProvider : MultipartFormDataStreamProvider
    {
        public CustomUploadMultipartFormProvider(string path) : base(path) { }

        public override string GetLocalFileName(HttpContentHeaders headers)
        {
            if (headers != null && headers.ContentDisposition != null)
            {
                return headers
                    .ContentDisposition
                    .FileName.TrimEnd('"').TrimStart('"');
            }

            return base.GetLocalFileName(headers);
        }


    }
}
